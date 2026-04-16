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
    val candidates = buildList {
        if (Metric.SevereWeather in enabled) add(severeWeather to WeatherReason.SevereWeather)
        if (Metric.Precipitation in enabled) add(precipitation to WeatherReason.Precipitation)
        if (Metric.Wind in enabled) add(wind to WeatherReason.Wind)
        if (Metric.Temperature in enabled) add(temperature to WeatherReason.Temperature)
        if (Metric.AirQuality in enabled) add(airQuality to WeatherReason.AirQuality)
    }
    return candidates.firstOrNull { it.first == ReasonValue.Outside }?.second
        ?: candidates.firstOrNull { it.first == ReasonValue.Near }?.second
}
