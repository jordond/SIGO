package app.sigot.forecast.domain

import app.sigot.core.domain.forecast.GetScoreUseCase
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.score.ForecastScore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class DefaultGetScoreUseCast(
    private val settingsRepo: SettingsRepo,
    private val scoreCalculator: ScoreCalculator,
) : GetScoreUseCase {
    override fun scoreFor(forecast: Forecast): Flow<ForecastScore> =
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
