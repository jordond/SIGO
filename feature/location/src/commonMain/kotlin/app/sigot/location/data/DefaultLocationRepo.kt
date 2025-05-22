package app.sigot.location.data

import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.foundation.NowProvider
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.location.LocationResult
import app.sigot.core.model.location.LocationResult.Success
import app.sigot.core.platform.LocationManager
import co.touchlab.kermit.Logger
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.hasPermission
import dev.jordond.compass.permissions.PermissionState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultLocationRepo(
    private val manager: LocationManager,
    private val settingsRepo: SettingsRepo,
    private val nowProvider: NowProvider,
) : LocationRepo {
    private val logger = Logger.withTag("CompassLocationManager")

    override fun hasPermission(): Boolean = manager.geolocator.hasPermission()

    override suspend fun requestPermission(): LocationPermissionStatus {
        val result = manager.permissions.requirePermissionFor(priority)
        return when (result) {
            PermissionState.NotDetermined -> LocationPermissionStatus.Unknown
            PermissionState.Granted -> LocationPermissionStatus.Granted
            PermissionState.Denied -> LocationPermissionStatus.Denied(false)
            PermissionState.DeniedForever -> LocationPermissionStatus.Denied(true)
        }
    }

    override suspend fun location(): LocationResult {
        if (!canGeolocate()) {
            return LocationResult.NotSupported
        }

        logger.d { "Getting location" }
        val lastLocation = withContext(Dispatchers.IO) {
            manager.geolocator
                .lastLocation(priority)
                .getOrNull()
                ?.toModel()
        }
        val result =
            if (lastLocation != null) {
                Logger.d { "Last location found" }
                Success(lastLocation)
            } else {
                Logger.d { "Getting current location" }
                withContext(Dispatchers.IO) {
                    manager.geolocator.current(priority).toResult()
                }
            }

        logger.d { "Location result: $result" }

        if (result is Success && manager.isGeocoderSupported && manager.geocoder.isAvailable()) {
            val place = withContext(Dispatchers.IO) {
                manager.geocoder
                    .reverse(result.location.coordinates())
                    .onFailed { error -> logger.e { "Failed to reverse geocode location: $error" } }
                    .getFirstOrNull()
            }

            if (place != null && !place.isEmpty) {
                logger.d { "Geocoding result: $place" }

                val name = place.locality ?: place.subAdministrativeArea ?: place.firstValue
                val updated = result.location.copy(name = name)
                settingsRepo.update { state ->
                    state.copy(
                        lastLocation = updated,
                        lastLocationUpdate = nowProvider.now(),
                    )
                }

                return Success(updated)
            } else {
                logger.i { "No geocoding result found for location: ${result.location}" }
            }
        }

        if (result is Success) {
            settingsRepo.update { state ->
                state.copy(
                    lastLocation = result.location,
                    lastLocationUpdate = nowProvider.now(),
                )
            }
        }

        return result
    }

    private suspend fun canGeolocate(): Boolean =
        withContext(Dispatchers.IO) {
            manager.isGeolocationSupported &&
                manager.geolocator.isAvailable().also { value ->
                    logger.d { "Geolocation availability: $value" }
                }
        }

    private fun GeolocatorResult.toResult(): LocationResult =
        when (this) {
            is GeolocatorResult.Error -> when (this) {
                is CancellationException -> throw this
                is GeolocatorResult.PermissionDenied -> {
                    LocationResult.NotAllowed(permanent = this.forever)
                }
                is GeolocatorResult.NotSupported -> LocationResult.NotSupported
                is GeolocatorResult.NotFound -> LocationResult.NotFound
                else -> {
                    logger.e { "Geolocation error: $this" }
                    LocationResult.Error
                }
            }
            is GeolocatorResult.Success -> {
                Success(
                    location = Location(
                        latitude = data.coordinates.latitude,
                        longitude = data.coordinates.longitude,
                    ),
                )
            }
        }

    private fun dev.jordond.compass.Location.toModel(): Location =
        Location(coordinates.latitude, coordinates.longitude)

    private fun Location.coordinates() = Coordinates(latitude, longitude)

    private companion object {
        // TODO: Use RemoteConfig
        private val priority = Priority.LowPower
    }
}
