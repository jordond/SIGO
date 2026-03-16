package now.shouldigooutside.ui

import androidx.lifecycle.viewModelScope
import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.initalize.Initializer
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.ui.navigation.AppStartDestination

internal class AppHostModel(
    private val initializer: Initializer,
    settingsRepo: SettingsRepo,
) : StateViewModel<AppHostModel.State>(State(settingsRepo.settings.value)) {
    init {
        viewModelScope.launch(Dispatchers.Default) { initializer.initialize() }

        settingsRepo.settings.mergeState { state, value ->
            if (!value.loaded) {
                state.copy(value)
            } else {
                val startDestination = if (value.hasCompletedOnboarding) {
                    AppStartDestination.Home
                } else {
                    AppStartDestination.Onboarding
                }

                state.copy(
                    settings = value,
                    uiState = State.UiState.Loaded(startDestination),
                )
            }
        }
    }

    data class State(
        val settings: Settings,
        val uiState: UiState = UiState.Loading,
    ) {
        val enableHaptics: Boolean = settings.enableHaptics

        sealed interface UiState {
            data object Loading : UiState

            data class Loaded(
                val startDestination: AppStartDestination,
            ) : UiState
        }
    }
}
