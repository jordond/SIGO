package app.sigot.core.model

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.score.ForecastScore
import app.sigot.core.model.score.Score

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
}

public data class ForecastPeriodData(
    val period: ForecastPeriod,
    val forecast: ForecastBlock,
    val score: Score,
)
