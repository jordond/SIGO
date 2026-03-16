package now.shouldigooutside.core.model.forecast

import kotlin.time.Instant

/**
 * A block of forecast data for a specific time, could be current, day, or hourly.
 *
 * @property instant Instant representation of the time of the forecast block.
 * @property humidity Humidity percentage.
 * @property cloudCoverPercent Cloud cover percentage.
 * @property temperature Temperature data for the forecast block.
 * @property precipitation Precipitation data for the forecast block.
 * @property wind Wind data for the forecast block.
 * @property pressure Atmospheric pressure in hPa.
 * @property uvIndex UV index value.
 * @property visibility Visibility in kilometers.
 * @property severeWeatherRisk Risk level of severe weather
 */
public data class ForecastBlock(
    val instant: Instant,
    val humidity: Double,
    val cloudCoverPercent: Int,
    val temperature: Temperature,
    val precipitation: Precipitation,
    val wind: Wind,
    val pressure: Double,
    val uvIndex: Int,
    val visibility: Double,
    val severeWeatherRisk: SevereWeatherRisk,
)
