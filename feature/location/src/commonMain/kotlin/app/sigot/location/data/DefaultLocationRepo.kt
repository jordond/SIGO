package app.sigot.location.data

import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.location.LocationResult
import app.sigot.core.domain.location.LocationResult.Success
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.location.Location
import app.sigot.core.platform.LocationManager
import co.touchlab.kermit.Logger
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Priority
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.TrackingStatus
import dev.jordond.compass.geolocation.hasPermission
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class DefaultLocationRepo(
    private val manager: LocationManager,
    private val geolocator: Geolocator,
    private val geocoder: Geocoder,
    private val settingsRepo: SettingsRepo,
    private val scope: CoroutineScope,
) : LocationRepo {
    private val logger = Logger.withTag("CompassLocationManager")

    override fun hasPermission(): Boolean = geolocator.hasPermission()

    override fun track(): Flow<TrackingStatus> =
        flow {
            if (!canGeolocate()) {
                logger.i { "Geolocation not supported" }
                return@flow emit(TrackingStatus.Error(GeolocatorResult.NotSupported))
            }

            scope.launch {
                geolocator.trackingStatus.collect { result ->
                    logger.d { "Tracking status: $result" }

                    if (result is TrackingStatus.Update) {
                        val location = result.location.toModel()
                        settingsRepo.update { it.copy(lastLocation = location) }

                        if (manager.isGeocoderSupported && geocoder.isAvailable()) {
                            val place = geocoder
                                .reverse(result.location.coordinates)
                                .onFailed { error ->
                                    logger.e { "Failed to reverse geocode location: $error" }
                                }.getFirstOrNull()

                            if (place != null && !place.isEmpty) {
                                logger.d { "Geocoding result: ${place.firstValue}" }

                                val updated = location.copy(name = place.firstValue)
                                settingsRepo.update { it.copy(lastLocation = updated) }
                            } else {
                                logger.i { "No geocoding result found for location: ${result.location}" }
                            }
                        }
                    }

                    emit(result)
                }
            }

            try {
                awaitCancellation()
            } finally {
                geolocator.stopTracking()
            }
        }

    override suspend fun location(resolve: Boolean): LocationResult {
        logger.d { "Getting current location" }
        val result = geolocator.current(Priority.Balanced).toResult()

        logger.d { "Current location result: $result" }

        if (resolve && result is Success && manager.isGeocoderSupported && geocoder.isAvailable()) {
            val place = geocoder
                .reverse(result.location.coordinates())
                .onFailed { error -> logger.e { "Failed to reverse geocode location: $error" } }
                .getFirstOrNull()

            if (place != null && !place.isEmpty) {
                logger.d { "Geocoding result: ${place.firstValue}" }

                val updated = result.location.copy(name = place.firstValue)
                settingsRepo.update { it.copy(lastLocation = updated) }

                return Success(updated)
            } else {
                logger.i { "No geocoding result found for location: ${result.location}" }
            }
        }

        if (result is Success) {
            settingsRepo.update { it.copy(lastLocation = result.location) }
        }

        return result
    }

    private suspend fun canGeolocate(): Boolean =
        withContext(Dispatchers.IO) {
            manager.isGeolocationSupported &&
                geolocator.isAvailable().also { value ->
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
}
