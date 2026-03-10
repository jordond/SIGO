package app.sigot.forecast.ui.details

import androidx.compose.runtime.Stable
import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.AsyncResult
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.score.ScoreResult
import dev.stateholder.extensions.viewmodel.UiStateViewModel

@Stable
internal class ForecastDetailsModel(
    settingsRepo: SettingsRepo,
    forecastStateHolder: ForecastStateHolder,
    scoreCalculator: ScoreCalculator,
) : UiStateViewModel<ForecastDetailsModel.State, Nothing>(
        run {
            val forecast = (forecastStateHolder.state.value as? AsyncResult.Success)?.data
            val preferences = settingsRepo.settings.value.preferences
            val scoreResult = forecast?.let { scoreCalculator.calculate(it, preferences).current.result }
            State(
                forecast = forecast,
                preferences = preferences,
                scoreResult = scoreResult,
            )
        },
    ) {
    init {
        settingsRepo.settings
            .mergeState { state, settings ->
                val scoreResult = state.forecast?.let {
                    scoreCalculator.calculate(it, settings.preferences).current.result
                }
                state.copy(preferences = settings.preferences, scoreResult = scoreResult)
            }
    }

    fun selectHour(block: ForecastBlock?) {
        updateState { state ->
            val selected = if (block == null || block == state.selected) null else block
            state.copy(selected = selected)
        }
    }

    data class State(
        val forecast: Forecast?,
        val preferences: Preferences,
        val selected: ForecastBlock? = null,
        val scoreResult: ScoreResult? = null,
    )
}
