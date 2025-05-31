package app.sigot.onboarding.ui.units

import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.units.Units
import app.sigot.onboarding.ui.units.OnboardingUnitsModel.State
import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class OnboardingUnitsModel(
    private val settingsRepo: SettingsRepo,
) : StateViewModel<State>(State(settingsRepo.settings.value.preferences.units)) {
    init {
        settingsRepo.settings
            .map { it.preferences.units }
            .distinctUntilChanged()
            .mergeState { state, units -> state.copy(units = units) }
    }

    fun update(units: Units) {
        settingsRepo.update { settings ->
            settings.updatePreferences(settings.preferences.copy(units = units))
        }
    }

    data class State(
        val units: Units,
    )
}
