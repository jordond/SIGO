package now.shouldigooutside.core.model.forecast

import kotlin.jvm.JvmInline

/**
 * Normalized Air Quality Index on a 1-10 scale.
 *
 * [value] of 0 means no data is available. Values 1-10 map to increasingly
 * poor air quality, from Good (1-2) through Hazardous (10).
 */
@JvmInline
public value class AirQuality(
    public val value: Int,
) {
    init {
        require(value in 0..10) { "AQI must be 0-10, was $value" }
    }

    public val hasData: Boolean get() = value > 0

    public operator fun compareTo(other: Int): Int = value.compareTo(other)

    public companion object {
        public val None: AirQuality = AirQuality(0)

        public fun from(value: Int?): AirQuality? = value?.coerceIn(0, 10)?.let(::AirQuality)
    }
}
