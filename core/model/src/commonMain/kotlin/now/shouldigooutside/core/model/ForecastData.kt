package now.shouldigooutside.core.model

import androidx.compose.runtime.Immutable
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.Score

@Immutable
public data class ForecastData(
    val forecast: Forecast,
    val score: ForecastScore,
) {
    public fun forPeriod(period: ForecastPeriod): ForecastPeriodData? {
        val block = when (period) {
            ForecastPeriod.Today -> forecast.today.block
            ForecastPeriod.Now -> forecast.current
            ForecastPeriod.NextHour -> forecast.today.hours.getOrNull(0)
            ForecastPeriod.NextHour2 -> forecast.today.hours.getOrNull(1)
            ForecastPeriod.NextHour3 -> forecast.today.hours.getOrNull(2)
            ForecastPeriod.Tomorrow -> forecast.days.getOrNull(0)?.block
        }

        val score = when (period) {
            ForecastPeriod.Today -> score.today
            ForecastPeriod.Now -> score.current
            ForecastPeriod.NextHour -> score.hours.getOrNull(0)
            ForecastPeriod.NextHour2 -> score.hours.getOrNull(1)
            ForecastPeriod.NextHour3 -> score.hours.getOrNull(2)
            ForecastPeriod.Tomorrow -> score.days.getOrNull(0)
        }

        return if (block == null || score == null) {
            null
        } else {
            ForecastPeriodData(period, block, score)
        }
    }

    public fun forBlock(block: ForecastBlock): Score? =
        when (block) {
            forecast.current -> {
                score.current
            }
            forecast.today.block -> {
                score.today
            }
            forecast.tomorrow?.block -> {
                score.days.getOrNull(0)
            }
            else -> {
                forecast.today.hours
                    .indexOfFirst { it == block }
                    .takeIf { it >= 0 }
                    ?.let { score.hours.getOrNull(it) }
            }
        }
}

public data class ForecastPeriodData(
    val period: ForecastPeriod,
    val forecast: ForecastBlock,
    val score: Score,
)
