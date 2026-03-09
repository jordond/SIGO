package app.sigot.forecast.ui

import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.AsyncResult
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.Units
import dev.stateholder.extensions.viewmodel.UiStateViewModel

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

    fun selectHour(index: Int?) {
        updateState { it.copy(selectedHourIndex = index) }
    }

    data class State(
        val forecast: Forecast?,
        val preferences: Preferences,
        val selectedHourIndex: Int? = null,
    ) {
        val units: Units get() = preferences.units
        val hours: List<ForecastBlock> get() = forecast?.today?.hours.orEmpty()
        val currentBlock: ForecastBlock? get() = forecast?.current
        val todayBlock: ForecastBlock? get() = forecast?.today?.block

        val selectedBlock: ForecastBlock?
            get() = selectedHourIndex?.let { hours.getOrNull(it) } ?: currentBlock

        val locationName: String get() = forecast?.location?.name.orEmpty()
    }
}
