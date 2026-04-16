package now.shouldigooutside.core.model.score

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.forecast.WeatherReason

@Immutable
public data class Reasons(
    val wind: ReasonValue,
    val temperature: ReasonValue,
    val precipitation: ReasonValue,
    val severeWeather: ReasonValue,
    val airQuality: ReasonValue,
)

private val AllMetrics: Set<Metric> = Metric.entries.toSet()

public fun Reasons.dominantReason(): WeatherReason? = dominantReason(AllMetrics)

public fun Reasons.dominantReason(enabled: Set<Metric>): WeatherReason? {
    if (Metric.SevereWeather in enabled &&
        severeWeather == ReasonValue.Outside
    ) {
        return WeatherReason.SevereWeather
    }
    if (Metric.Precipitation in enabled &&
        precipitation == ReasonValue.Outside
    ) {
        return WeatherReason.Precipitation
    }
    if (Metric.Wind in enabled && wind == ReasonValue.Outside) return WeatherReason.Wind
    if (Metric.Temperature in enabled &&
        temperature == ReasonValue.Outside
    ) {
        return WeatherReason.Temperature
    }
    if (Metric.AirQuality in enabled &&
        airQuality == ReasonValue.Outside
    ) {
        return WeatherReason.AirQuality
    }
    if (Metric.SevereWeather in enabled &&
        severeWeather == ReasonValue.Near
    ) {
        return WeatherReason.SevereWeather
    }
    if (Metric.Precipitation in enabled &&
        precipitation == ReasonValue.Near
    ) {
        return WeatherReason.Precipitation
    }
    if (Metric.Wind in enabled && wind == ReasonValue.Near) return WeatherReason.Wind
    if (Metric.Temperature in enabled &&
        temperature == ReasonValue.Near
    ) {
        return WeatherReason.Temperature
    }
    if (Metric.AirQuality in enabled &&
        airQuality == ReasonValue.Near
    ) {
        return WeatherReason.AirQuality
    }
    return null
}
