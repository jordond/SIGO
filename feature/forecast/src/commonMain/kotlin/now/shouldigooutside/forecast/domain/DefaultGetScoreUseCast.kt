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

internal class DefaultGetScoreUseCast(
    private val settingsRepo: SettingsRepo,
    private val scoreCalculator: ScoreCalculator,
) : GetScoreUseCase {
    override fun scoreFor(forecast: Forecast): ForecastScore =
        scoreCalculator.calculate(forecast, settingsRepo.settings.value.preferences)

    override fun scoreForFlow(forecast: Forecast): Flow<ForecastScore> =
        flow {
            settingsRepo.settings
                .map { it.preferences }
                .distinctUntilChanged()
                .collect { preferences ->
                    val result = scoreCalculator.calculate(forecast, preferences)
                    emit(result)
                }
        }
}
