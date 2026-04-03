package now.shouldigooutside.core.model.forecast

import androidx.compose.runtime.Immutable

/**
 * Represents a daily forecast, including the forecast block and a list of hourly forecasts.
 *
 * @property block The forecast block for the day.
 * @property hours A list of hourly forecasts for the day.
 */
@Immutable
public data class ForecastDay(
    val block: ForecastBlock,
    val hours: List<ForecastBlock>,
)
