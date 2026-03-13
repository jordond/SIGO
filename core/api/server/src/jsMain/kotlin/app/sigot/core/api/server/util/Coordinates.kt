package app.sigot.core.api.server.util

import app.sigot.core.api.server.exception.BadRequestException
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Round a coordinate (lat/lon) to the specified number of decimal places.
 * Defaults to 2 decimal places for cache-friendly bucketing.
 */
public fun Double.roundCoordinate(decimals: Int = 2): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt().toDouble() / factor
}

/**
 * Validate that latitude and longitude are within valid ranges.
 * Throws [BadRequestException] with validation errors if out of bounds.
 */
public fun validateCoordinates(
    lat: Double,
    lon: Double,
) {
    val errors = mutableListOf<String>()
    if (lat < -90.0 || lat > 90.0) {
        errors.add("lat must be between -90 and 90")
    }
    if (lon < -180.0 || lon > 180.0) {
        errors.add("lon must be between -180 and 180")
    }
    if (errors.isNotEmpty()) {
        throw BadRequestException(validation = errors)
    }
}
