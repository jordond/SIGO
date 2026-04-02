package now.shouldigooutside.core.model.score

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.forecast.ForecastPeriod

@Immutable
public data class ForecastScore(
    val current: Score,
    val hours: List<Score>,
    val today: Score,
    val days: List<Score>,
)

public fun ForecastScore.scoreForPeriod(period: ForecastPeriod): Score? =
    when (period) {
        ForecastPeriod.Today -> today
        ForecastPeriod.Now -> current
        ForecastPeriod.NextHour -> hours.getOrNull(0)
        ForecastPeriod.NextHour2 -> hours.getOrNull(1)
        ForecastPeriod.NextHour3 -> hours.getOrNull(2)
        ForecastPeriod.Tomorrow -> days.getOrNull(0)
    }
