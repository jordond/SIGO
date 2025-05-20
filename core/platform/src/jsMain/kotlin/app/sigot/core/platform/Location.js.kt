package app.sigot.core.platform

import dev.jordond.compass.geolocation.BrowserGeolocator
import dev.jordond.compass.geolocation.Geolocator

internal actual val geolocationSupported: Boolean = true

internal actual fun createGeolocator(): Geolocator = BrowserGeolocator()
