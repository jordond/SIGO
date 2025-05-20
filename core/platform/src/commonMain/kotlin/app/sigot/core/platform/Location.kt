package app.sigot.core.platform

import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geolocation.Geolocator

public interface LocationManager {
    public val isGeolocationSupported: Boolean
    public val isGeocoderSupported: Boolean
}

internal class DefaultLocationManager : LocationManager {
    override val isGeolocationSupported: Boolean = geolocationSupported
    override val isGeocoderSupported: Boolean = geocoderSupported
}

internal expect val geolocationSupported: Boolean

internal expect val geocoderSupported: Boolean

internal expect fun createGeolocator(): Geolocator

internal expect fun createGeocoder(): Geocoder
