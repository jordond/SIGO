package app.sigot.onboarding.ui

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.location.Location
import app.sigot.onboarding.ui.navigation.OnboardingDestination
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val destinations = listOfNotNull(
    OnboardingDestination.Welcome,
    OnboardingDestination.Units,
    OnboardingDestination.Preferences,
    OnboardingDestination.Location,
    OnboardingDestination.Summary,
)

internal class OnboardingModel(
    private val settingsRepo: SettingsRepo,
) : UiStateViewModel<OnboardingModel.State, OnboardingModel.Event>(State()) {
    init {
        settingsRepo.settings
            .map { it.lastLocation }
            .distinctUntilChanged()
            .mergeState { state, value -> state.copy(location = value) }
    }

    fun updateDestination(entry: NavBackStackEntry?) {
        val destination = destinations.firstOrNull { destination ->
            entry?.destination?.hasRoute(destination::class) == true
        } ?: OnboardingDestination.Welcome

        updateState { it.copy(currentDestination = destination) }
    }

    fun onClick() {
        val current = state.value.currentDestination

        val index = destinations.indexOf(current)
        if (index == destinations.lastIndex) {
            settingsRepo.update { it.copy(hasCompletedOnboarding = true) }
            emit(Event.Finish)
        } else if (index != -1) {
            emitNavigation(destinations[index + 1])
        }
    }

    fun confirmLocationDialog(confirm: Boolean) {
        updateState { it.copy(showLocationWarning = false, didShowLocationWarning = confirm) }
        if (confirm) {
            emitNavigation(OnboardingDestination.Summary)
        }
    }

    private fun emitNavigation(destination: OnboardingDestination) {
        if (destination is OnboardingDestination.Summary) {
            if (state.value.location == null && !state.value.didShowLocationWarning) {
                return updateState { it.copy(showLocationWarning = true) }
            }
        }

        emit(Event.ToScreen(destination))
    }

    data class State(
        val location: Location? = null,
        val currentDestination: OnboardingDestination = OnboardingDestination.Welcome,
        val showLocationWarning: Boolean = false,
        val didShowLocationWarning: Boolean = false,
    )

    sealed interface Event {
        data class ToScreen(
            val destination: OnboardingDestination,
        ) : Event

        data object Finish : Event
    }
}
