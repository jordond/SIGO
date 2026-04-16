package now.shouldigooutside.core.model.forecast

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.score.dominantReason
import now.shouldigooutside.core.model.units.Units
import kotlin.time.Duration.Companion.hours
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
        ForecastPeriod.NextHour4 -> today.hours.getOrNull(3)
        ForecastPeriod.NextHour5 -> today.hours.getOrNull(4)
        ForecastPeriod.Tomorrow -> days.getOrNull(0)?.block
    }

@Immutable
public data class WeatherWindow(
    val start: Instant,
    val end: Instant,
)

/**
 * Finds contiguous time windows where the hourly score is [ScoreResult.Yes].
 *
 * Pairs [today]'s hourly blocks with [score]'s hourly scores (truncated to the shorter list)
 * and groups consecutive `Yes` results into [WeatherWindow]s.
 *
 * @param score The forecast score to evaluate, or `null` to return an empty list.
 * @return An ordered list of good weather windows, or an empty list if [score] is `null`
 *   or no hours scored `Yes`.
 */
public fun Forecast.goodWeatherWindows(score: ForecastScore?): List<WeatherWindow> =
    weatherWindows(score, ScoreResult.Yes)

/**
 * Finds contiguous time windows where the hourly score equals [result].
 *
 * Pairs [today]'s hourly blocks with [score]'s hourly scores (truncated to the shorter list)
 * and groups consecutive matching results into [WeatherWindow]s.
 */
public fun Forecast.weatherWindows(
    score: ForecastScore?,
    result: ScoreResult,
): List<WeatherWindow> =
    buildList {
        if (score == null) return emptyList()
        val paired = today.hours.zip(score.hours)
        var windowStart: Instant? = null
        for ((hour, hourScore) in paired) {
            if (hourScore.result == result) {
                if (windowStart == null) windowStart = hour.instant
            } else if (windowStart != null) {
                add(WeatherWindow(start = windowStart, end = hour.instant))
                windowStart = null
            }
        }
        if (windowStart != null) {
            add(WeatherWindow(start = windowStart, end = paired.last().first.instant + 1.hours))
        }
    }

/** The banner to show on the forecast home screen, or `null` when inputs are unavailable. */
public fun Forecast.weatherBannerInfo(
    score: ForecastScore?,
    currentResult: ScoreResult?,
    activity: Activity,
    now: Instant,
): WeatherBannerInfo? {
    if (score == null || currentResult == null) return null

    return when (currentResult) {
        ScoreResult.Yes -> goNowBanner(score, activity, now)
        ScoreResult.No, ScoreResult.Maybe -> nextWindowBanner(score, now)
    }
}

private fun Forecast.goNowBanner(
    score: ForecastScore,
    activity: Activity,
    now: Instant,
): WeatherBannerInfo.GoNow? {
    val paired = today.hours.zip(score.hours)
    if (paired.isEmpty()) return null

    val transition = paired
        .asSequence()
        .dropWhile { (hour, _) -> hour.instant < now }
        .firstOrNull { (_, hourScore) -> hourScore.result != ScoreResult.Yes }

    val endsAt = transition?.first?.instant ?: (paired.last().first.instant + 1.hours)
    val reason = transition?.second?.reasons?.dominantReason()

    return WeatherBannerInfo.GoNow(endsAt = endsAt, reason = reason, activity = activity)
}

private val nextWindowPriority = listOf(
    ScoreResult.Yes to WindowQuality.Good,
    ScoreResult.Maybe to WindowQuality.Borderline,
)

private fun Forecast.nextWindowBanner(
    score: ForecastScore,
    now: Instant,
): WeatherBannerInfo =
    nextWindowPriority.firstNotNullOfOrNull { (result, quality) ->
        // `it.end > now` (rather than `start > now`) surfaces a window that is currently
        // ongoing or starts at `now`. Hours from the API are pre-filtered to future-only,
        // so a window's start instant can be slightly less than `Clock.System.now()` due
        // to clock drift between fetch and recomputation.
        weatherWindows(score, result)
            .firstOrNull { it.end > now }
            ?.let { WeatherBannerInfo.NextWindow(window = it, quality = quality) }
    } ?: WeatherBannerInfo.NoWindowToday

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
