package now.shouldigooutside.forecast.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import now.shouldigooutside.core.domain.forecast.GetScoreUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.score.ForecastScore

// TODO: Need to use either the selected Activity, or do all activities
internal class DefaultGetScoreUseCase(
    private val settingsRepo: SettingsRepo,
    private val scoreCalculator: ScoreCalculator,
) : GetScoreUseCase {
    override fun scoreFor(forecast: Forecast): ForecastScore {
        val settings = settingsRepo.settings.value
        return scoreCalculator.calculate(forecast, settings.preferences, settings.includeAirQuality)
    }

    override fun scoreForFlow(forecast: Forecast): Flow<ForecastScore> =
        flow {
            settingsRepo.settings
                .map { it.preferences to it.includeAirQuality }
                .distinctUntilChanged()
                .collect { (preferences, includeAirQuality) ->
                    val result = scoreCalculator.calculate(forecast, preferences, includeAirQuality)
                    emit(result)
                }
        }
}
