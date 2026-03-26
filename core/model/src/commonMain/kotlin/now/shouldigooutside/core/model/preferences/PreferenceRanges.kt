package now.shouldigooutside.core.model.preferences

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.model.units.convertTemperature
import now.shouldigooutside.core.model.units.convertWindSpeed

private const val DEFAULT_MIN_TEMP_RANGE = -30.0
private const val DEFAULT_MAX_TEMP_RANGE = 40.0
private const val DEFAULT_MAX_WIND_SPEED = 50.0

@Immutable
public data class PreferenceRanges(
    val temperature: ClosedFloatingPointRange<Float>,
    val maxWindSpeed: Float,
) {
    public companion object {
        public fun from(
            units: Units,
            temperatureStart: Double = DEFAULT_MIN_TEMP_RANGE,
            temperatureEnd: Double = DEFAULT_MAX_TEMP_RANGE,
            windSpeed: Double = DEFAULT_MAX_WIND_SPEED,
        ): PreferenceRanges {
            val startTempRange =
                convertTemperature(
                    value = temperatureStart,
                    from = TemperatureUnit.Celsius,
                    target = units.temperature,
                ).toFloat()

            val endTempRange =
                convertTemperature(
                    value = temperatureEnd,
                    from = TemperatureUnit.Celsius,
                    target = units.temperature,
                ).toFloat()

            return PreferenceRanges(
                temperature = startTempRange..endTempRange,
                maxWindSpeed = convertWindSpeed(
                    value = windSpeed,
                    from = WindSpeedUnit.KilometerPerHour,
                    target = units.windSpeed,
                ).toFloat(),
            )
        }
    }
}
