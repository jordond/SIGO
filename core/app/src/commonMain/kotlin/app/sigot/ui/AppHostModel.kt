package app.sigot.ui

import androidx.lifecycle.viewModelScope
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.foundation.initalize.Initializer
import app.sigot.core.model.settings.Settings
import app.sigot.core.model.ui.ThemeMode
import app.sigot.ui.navigation.AppStartDestination
import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class AppHostModel(
    private val initializer: Initializer,
    settingsRepo: SettingsRepo,
) : StateViewModel<AppHostModel.State>(settingsRepo.settings.value.toState()) {
    init {
        viewModelScope.launch(Dispatchers.Default) { initializer.initialize() }

        settingsRepo.settings.mergeState { _, value ->
            value.toState().let { newState ->
                if (!value.loaded) {
                    newState
                } else {
                    val startDestination = if (value.hasCompletedOnboarding) {
                        AppStartDestination.Home
                    } else {
                        AppStartDestination.Onboarding
                    }

                    newState.copy(uiState = State.UiState.Loaded(startDestination))
                }
            }
        }
    }

    data class State(
        val themeMode: ThemeMode,
        val settingsLoaded: Boolean = false,
        val uiState: UiState = UiState.Loading,
        val enableHaptics: Boolean = false,
    ) {
        sealed interface UiState {
            data object Loading : UiState

            data class Loaded(
                val startDestination: AppStartDestination,
            ) : UiState
        }
    }
}

private fun Settings.toState(): AppHostModel.State =
    AppHostModel.State(
        themeMode = themeMode,
        settingsLoaded = loaded,
        enableHaptics = enableHaptics,
    )
