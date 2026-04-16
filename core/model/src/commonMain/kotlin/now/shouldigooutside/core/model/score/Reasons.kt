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

public fun Reasons.dominantReason(): WeatherReason? {
    if (severeWeather == ReasonValue.Outside) return WeatherReason.SevereWeather
    if (precipitation == ReasonValue.Outside) return WeatherReason.Precipitation
    if (wind == ReasonValue.Outside) return WeatherReason.Wind
    if (temperature == ReasonValue.Outside) return WeatherReason.Temperature
    if (airQuality == ReasonValue.Outside) return WeatherReason.AirQuality
    if (severeWeather == ReasonValue.Near) return WeatherReason.SevereWeather
    if (precipitation == ReasonValue.Near) return WeatherReason.Precipitation
    if (wind == ReasonValue.Near) return WeatherReason.Wind
    if (temperature == ReasonValue.Near) return WeatherReason.Temperature
    if (airQuality == ReasonValue.Near) return WeatherReason.AirQuality
    return null
}
