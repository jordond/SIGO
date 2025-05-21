package app.sigot.core.platform

import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.permissions.LocationPermissionController

public interface LocationManager {
    public val geocoder: Geocoder
    public val geolocator: Geolocator
    public val permissions: LocationPermissionController

    public val isGeolocationSupported: Boolean
    public val isGeocoderSupported: Boolean
}

internal class DefaultLocationManager(
    override val geocoder: Geocoder,
    override val geolocator: Geolocator,
    override val permissions: LocationPermissionController,
) : LocationManager {
    override val isGeolocationSupported: Boolean = geolocationSupported
    override val isGeocoderSupported: Boolean = geocoderSupported
}

internal expect val geolocationSupported: Boolean

internal expect val geocoderSupported: Boolean

internal expect fun createGeolocator(): Geolocator

internal expect fun createGeocoder(): Geocoder

internal expect fun locationPermissionController(): LocationPermissionController
