package app.sigot.core.platform

import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.MobileGeolocator

internal actual val geolocationSupported: Boolean = true

internal actual val geocoderSupported: Boolean = true

internal actual fun createGeolocator(): Geolocator = MobileGeolocator()

internal actual fun createGeocoder(): Geocoder = MobileGeocoder()
