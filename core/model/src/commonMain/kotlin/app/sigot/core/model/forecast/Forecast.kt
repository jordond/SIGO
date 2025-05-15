package app.sigot.core.model.forecast

/**
 * Represents a weather forecast.
 *
 * @property location The location for which the forecast is provided.
 * @property current The current weather conditions.
 * @property daily A list of daily forecasts.
 * @property alerts A list of weather alerts.
 */
public data class Forecast(
    val location: Location,
    val current: ForecastBlock,
    val daily: List<ForecastDay>,
    val alerts: List<Alert>,
)
