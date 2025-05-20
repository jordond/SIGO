package app.sigot.core.platform

import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Locator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

private val locator = object : Locator {
    override val locationUpdates: Flow<Location> = emptyFlow()

    override suspend fun lastLocation(priority: Priority): Location? = null

    override suspend fun isAvailable(): Boolean = false

    override suspend fun current(priority: Priority): Location {
        error("Not supported on the JVM")
    }

    override suspend fun track(request: LocationRequest): Flow<Location> = emptyFlow()

    override fun stopTracking() {}
}

internal actual val geolocationSupported: Boolean = false

internal actual fun createGeolocator(): Geolocator = Geolocator(locator)
