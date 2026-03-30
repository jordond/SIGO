package now.shouldigooutside.forecast.domain

import now.shouldigooutside.core.config.AppConfigRepo
import now.shouldigooutside.core.config.model.PrecipitationConfig
import now.shouldigooutside.core.domain.forecast.DefaultScoreCalculator
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ForecastScore

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
        includeAirQuality: Boolean,
    ): ForecastScore = calculator.calculate(forecast, preferences, includeAirQuality)
}
