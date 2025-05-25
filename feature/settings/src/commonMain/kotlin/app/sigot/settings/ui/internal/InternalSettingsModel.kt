package app.sigot.settings.ui.internal

import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.settings.Settings
import dev.stateholder.extensions.viewmodel.UiStateViewModel

internal class InternalSettingsModel(
    settingsRepo: SettingsRepo,
) : UiStateViewModel<InternalSettingsModel.State, InternalSettingsModel.Event>(
        State(settingsRepo.settings.value),
    ) {
    init {
        settingsRepo.settings.mergeState { state, value -> state.copy(settings = value) }
    }

    data class State(
        val settings: Settings,
    )

    sealed interface Event
}
