package now.shouldigooutside.core.model.preferences

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.score.Metric

/**
 * Temperature and wind speed values are always stored in Metric (Celsius, km/h).
 * UI layers convert to/from the user's display units at the boundary.
 */
@Immutable
public data class Preferences(
    public val minTemperature: Double,
    public val maxTemperature: Double,
    public val includeApparentTemperature: Boolean,
    public val windSpeed: Double,
    public val rain: Boolean,
    public val snow: Boolean,
    public val maxAqi: AirQuality,
    public val temperatureEnabled: Boolean = true,
    public val windEnabled: Boolean = true,
    public val precipitationEnabled: Boolean = true,
    public val aqiEnabled: Boolean = true,
) {
    public companion object {
        public val default: Preferences = Preferences(
            minTemperature = 5.0,
            maxTemperature = 35.0,
            includeApparentTemperature = false,
            windSpeed = 30.0,
            rain = false,
            snow = false,
            maxAqi = AirQuality(3),
        )

        public fun defaultFor(activity: Activity): Preferences =
            when (activity) {
                is Activity.General -> default
                is Activity.Walking -> default.copy(
                    minTemperature = -10.0,
                    maxTemperature = 30.0,
                    windSpeed = 35.0,
                    rain = true,
                    snow = true,
                    maxAqi = AirQuality(5),
                )
                is Activity.Running -> default.copy(
                    minTemperature = 10.0,
                    maxTemperature = 30.0,
                    windSpeed = 25.0,
                    rain = false,
                    snow = false,
                    maxAqi = AirQuality(2),
                )
                is Activity.Cycling -> default.copy(
                    minTemperature = 10.0,
                    maxTemperature = 30.0,
                    windSpeed = 25.0,
                    rain = false,
                    snow = false,
                    maxAqi = AirQuality(2),
                )
                is Activity.Hiking -> default.copy(
                    minTemperature = 5.0,
                    maxTemperature = 30.0,
                    windSpeed = 35.0,
                    rain = true,
                    snow = true,
                    maxAqi = AirQuality(3),
                )
                is Activity.Swimming -> default.copy(
                    minTemperature = 20.0,
                    maxTemperature = 35.0,
                    windSpeed = 30.0,
                    rain = false,
                    snow = false,
                    maxAqi = AirQuality(6),
                )
                is Activity.Custom -> default
            }
    }
}

public fun Preferences.enabledMetrics(includeAirQuality: Boolean): Set<Metric> =
    buildSet {
        if (temperatureEnabled) add(Metric.Temperature)
        if (windEnabled) add(Metric.Wind)
        if (precipitationEnabled) add(Metric.Precipitation)
        if (includeAirQuality && aqiEnabled) add(Metric.AirQuality)
        add(Metric.SevereWeather)
    }
