package now.shouldigooutside.core.model.forecast

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Instant

/**
 * Represents a weather forecast.
 *
 * @property location The location for which the forecast is provided.
 * @property current The current weather conditions.
 * @property today The forecast for today.
 * @property days A list of daily forecasts starting the day after [instant].
 * @property alerts A list of weather alerts.
 */
@Immutable
public data class Forecast(
    val location: Location,
    val current: ForecastBlock,
    val today: ForecastDay,
    val days: List<ForecastDay>,
    val alerts: List<Alert>,
    val units: Units = Units.SI,
    val instant: Instant,
) {
    val tomorrow: ForecastDay? get() = days.getOrNull(0)

    public fun hour(index: Int): ForecastBlock? = today.hours.getOrNull(index)
}
