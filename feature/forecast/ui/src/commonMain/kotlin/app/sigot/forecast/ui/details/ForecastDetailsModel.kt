package app.sigot.forecast.ui.details

import androidx.compose.runtime.Stable
import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.AsyncResult
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.preferences.Preferences
import dev.stateholder.extensions.viewmodel.UiStateViewModel

@Stable
internal class ForecastDetailsModel(
    settingsRepo: SettingsRepo,
    forecastStateHolder: ForecastStateHolder,
) : UiStateViewModel<ForecastDetailsModel.State, Nothing>(
        State(
            forecast = (forecastStateHolder.state.value as? AsyncResult.Success)?.data,
            preferences = settingsRepo.settings.value.preferences,
        ),
    ) {
    init {
        settingsRepo.settings
            .mergeState { state, settings ->
                state.copy(preferences = settings.preferences)
            }
    }

    fun selectHour(block: ForecastBlock) {
        updateState { state ->
            val selected = if (block == state.selected) state.forecast?.current else block
            state.copy(selected = selected)
        }
    }

    data class State(
        val forecast: Forecast?,
        val preferences: Preferences,
        val selected: ForecastBlock? = null,
    )
}
