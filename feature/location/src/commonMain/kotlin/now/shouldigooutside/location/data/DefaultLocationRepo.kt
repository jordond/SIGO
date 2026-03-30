package now.shouldigooutside.location.data

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
import now.shouldigooutside.core.config.AppConfigRepo
import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.NowProvider
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.location.LocationPermissionStatus
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.model.location.LocationResult.Success
import now.shouldigooutside.core.platform.LocationManager
import kotlin.time.Duration

internal class DefaultLocationRepo(
    private val manager: LocationManager,
    private val settingsRepo: SettingsRepo,
    private val nowProvider: NowProvider,
    private val appConfigRepo: AppConfigRepo,
) : LocationRepo {
    private val logger = Logger.withTag("LocationRepo")

    private val maxLocationAge: Duration
        get() = appConfigRepo.value.locationCacheAge

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
        val settings = settingsRepo.settings.value
        val customLocation = settings.customLocation
        if (settings.useCustomLocation && customLocation != null) {
            logger.d { "Using custom location: ${customLocation.name}" }
            return Success(customLocation)
        }

        val (savedLocation, timestamp) = settings.run { lastLocation to lastLocationUpdate }

        if (savedLocation != null && timestamp != null) {
            val age = nowProvider.now() - timestamp
            logger.d { "Max cache age: ${maxLocationAge.inWholeMinutes} mins" }
            logger.d { "Cached location age: ${age.inWholeMinutes} mins" }
            if (age < maxLocationAge) {
                logger.d { "Using cached location" }
                return Success(savedLocation)
            }
        }

        if (!canGeolocate()) {
            return LocationResult.NotSupported()
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

        if (result is LocationResult.Failed && savedLocation != null) {
            when (result) {
                is LocationResult.NotFound -> {
                    logger.w { "GPS not ready, falling back to stale cached location: ${savedLocation.name}" }
                }
                is LocationResult.Error -> {
                    logger.w { "GPS error, falling back to stale cached location: ${savedLocation.name}" }
                }
                else -> {
                    return result
                } // NotAllowed / NotSupported should not fall back
            }
            val enriched = enrichWithGeocoding(savedLocation)
            return Success(enriched)
        }

        if (result is Success) {
            val enriched = enrichWithGeocoding(result.location)
            settingsRepo.update { state ->
                state.copy(
                    lastLocation = enriched,
                    lastLocationUpdate = nowProvider.now(),
                )
            }
            return Success(enriched)
        }

        return result
    }

    private suspend fun enrichWithGeocoding(location: Location): Location {
        if (!manager.isGeocoderSupported || !manager.geocoder.isAvailable()) return location

        val place = withContext(Dispatchers.IO) {
            manager.geocoder
                .reverse(location.coordinates())
                .onFailed { error -> logger.e { "Failed to reverse geocode location: $error" } }
                .getFirstOrNull()
        }

        if (place == null || place.isEmpty) {
            logger.i { "No geocoding result found for location: $location" }
            return location
        }

        logger.d { "Geocoding result: $place" }
        return location.copy(
            name = place.locality ?: place.subAdministrativeArea ?: place.firstValue,
            administrativeArea = place.administrativeArea,
            country = place.country,
        )
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
            is GeolocatorResult.Error -> {
                when (this) {
                    is CancellationException -> {
                        throw this
                    }
                    is GeolocatorResult.PermissionDenied -> {
                        LocationResult.NotAllowed(permanent = this.forever)
                    }
                    is GeolocatorResult.NotSupported -> {
                        LocationResult.NotSupported()
                    }
                    is GeolocatorResult.NotFound -> {
                        LocationResult.NotFound()
                    }
                    else -> {
                        logger.e { "Geolocation error: $this" }
                        LocationResult.Error()
                    }
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
