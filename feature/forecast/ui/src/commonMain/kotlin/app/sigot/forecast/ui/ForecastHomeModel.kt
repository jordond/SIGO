package app.sigot.forecast.ui

import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.location.Location
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class ForecastHomeModel(
    settingsRepo: SettingsRepo,
) : UiStateViewModel<ForecastHomeModel.State, ForecastHomeModel.Event>(
        State(
            location = settingsRepo.settings.value.lastLocation,
        ),
    ) {
    init {
        settingsRepo.settings
            .map { it.lastLocation }
            .distinctUntilChanged()
            .mergeState { state, value -> state.copy(location = value) }
    }

    data class State(
        val location: Location?,
    )

    sealed interface Event
}
