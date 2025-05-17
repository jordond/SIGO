package app.sigot.onboarding.ui.preferences

import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.preferences.Preferences
import app.sigot.onboarding.ui.preferences.PreferencesModel.Event
import app.sigot.onboarding.ui.preferences.PreferencesModel.State
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class PreferencesModel(
    private val settingsRepo: SettingsRepo,
) : UiStateViewModel<State, Event>(State(settingsRepo.settings.value.preferences)) {
    init {
        settingsRepo.settings
            .map { it.preferences }
            .distinctUntilChanged()
            .mergeState { state, preferences -> state.copy(preferences = preferences) }
    }

    fun update(preferences: Preferences) {
        settingsRepo.update { settings ->
            settings.copy(preferences = preferences)
        }
    }

    data class State(
        val preferences: Preferences,
    )

    sealed interface Event
}
