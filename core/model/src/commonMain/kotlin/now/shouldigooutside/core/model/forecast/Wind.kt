package now.shouldigooutside.core.model.forecast

/**
 * Represents the wind data for a specific time and location.
 *
 * @property speed Wind speed in base units.
 * @property gust Wind gust speed in base units.
 * @property directionDegree Wind direction in degree 0-360..
 * @property maxSpeed Maximum wind speed in base units, if not available, it will be the same as [speed].
 * @property meanSpeed Mean wind speed in base units, if not available, it will be the same as [speed].
 * @property minSpeed Minimum wind speed in base units, if not available, it will be the same as [speed].
 */
public data class Wind(
    val speed: Double,
    val gust: Double,
    val directionDegree: Double,
    val maxSpeed: Double,
    val meanSpeed: Double,
    val minSpeed: Double,
) {
    public constructor(
        speed: Double,
        gust: Double?,
        directionDegree: Double,
        maxSpeed: Double?,
        meanSpeed: Double?,
        minSpeed: Double?,
    ) : this(
        speed = speed,
        gust = gust ?: speed,
        directionDegree = directionDegree,
        maxSpeed = maxSpeed ?: speed,
        meanSpeed = meanSpeed ?: speed,
        minSpeed = minSpeed ?: speed,
    )
}
