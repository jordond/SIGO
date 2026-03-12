package app.sigot.core.platform

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.mobile
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.MobileGeolocator
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.MobileLocationPermissionController

internal actual val geolocationSupported: Boolean = true

internal actual val geocoderSupported: Boolean = true

internal actual fun createAutocomplete(): Autocomplete<Place> = Autocomplete.mobile()

internal actual fun createGeolocator(): Geolocator = MobileGeolocator(locationPermissionController())

internal actual fun createGeocoder(): Geocoder = MobileGeocoder()

internal actual fun locationPermissionController(): LocationPermissionController =
    MobileLocationPermissionController()
