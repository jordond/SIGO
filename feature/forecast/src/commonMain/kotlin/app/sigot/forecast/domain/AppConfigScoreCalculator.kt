package app.sigot.forecast.domain

import app.sigot.core.config.AppConfigRepo
import app.sigot.core.config.model.PrecipitationConfig
import app.sigot.core.domain.forecast.DefaultScoreCalculator
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.score.ForecastScore

internal class AppConfigScoreCalculator(
    private val appConfigRepo: AppConfigRepo,
) : ScoreCalculator {
    private val precipitationConfig: PrecipitationConfig
        get() = appConfigRepo.value.precipitation

    private val calculator by lazy {
        DefaultScoreCalculator(
            maxChance = precipitationConfig.maxChance,
            lowAmountMm = precipitationConfig.lowAmountMm,
            moderateAmountMm = precipitationConfig.moderateAmountMm,
            nearPercent = appConfigRepo.value.scoreNearPercent,
            maxNearReasons = appConfigRepo.value.scoreMaxNearReasons,
        )
    }

    override fun calculate(
        forecast: Forecast,
        preferences: Preferences,
    ): ForecastScore = calculator.calculate(forecast, preferences)
}
