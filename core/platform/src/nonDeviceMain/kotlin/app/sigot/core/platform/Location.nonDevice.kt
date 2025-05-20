package app.sigot.core.platform

import dev.jordond.compass.geocoder.Geocoder

internal actual val geocoderSupported: Boolean = false

internal actual fun createGeocoder(): Geocoder =
    throw UnsupportedOperationException("Geocoder is not supported on this platform")
