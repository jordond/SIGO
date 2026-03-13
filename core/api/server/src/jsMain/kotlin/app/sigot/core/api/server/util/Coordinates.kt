package app.sigot.core.api.server.util

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
