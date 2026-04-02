package now.shouldigooutside.core.model.forecast

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
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

public fun Forecast.blockForPeriod(period: ForecastPeriod): ForecastBlock? =
    when (period) {
        ForecastPeriod.Today -> today.block
        ForecastPeriod.Now -> current
        ForecastPeriod.NextHour -> today.hours.getOrNull(0)
        ForecastPeriod.NextHour2 -> today.hours.getOrNull(1)
        ForecastPeriod.NextHour3 -> today.hours.getOrNull(2)
        ForecastPeriod.Tomorrow -> days.getOrNull(0)?.block
    }

public data class WeatherWindow(
    val start: Instant,
    val end: Instant,
)

public fun Forecast.goodWeatherWindows(score: ForecastScore): List<WeatherWindow> {
    val hours = today.hours
    val scores = score.hours
    if (hours.isEmpty() || scores.isEmpty()) return emptyList()

    val windows = mutableListOf<WeatherWindow>()
    var windowStart: Instant? = null

    for (i in hours.indices) {
        val hourScore = scores.getOrNull(i) ?: break
        if (hourScore.result == ScoreResult.Yes) {
            if (windowStart == null) windowStart = hours[i].instant
        } else {
            if (windowStart != null) {
                windows += WeatherWindow(start = windowStart, end = hours[i].instant)
                windowStart = null
            }
        }
    }

    if (windowStart != null) {
        windows += WeatherWindow(start = windowStart, end = hours.last().instant)
    }

    return windows
}

public fun Forecast.scoreForBlock(
    block: ForecastBlock,
    score: ForecastScore,
): Score? =
    when (block) {
        current -> {
            score.current
        }
        today.block -> {
            score.today
        }
        tomorrow?.block -> {
            score.days.getOrNull(0)
        }
        else -> {
            today.hours
                .indexOfFirst { it == block }
                .takeIf { it >= 0 }
                ?.let { score.hours.getOrNull(it) }
        }
    }
