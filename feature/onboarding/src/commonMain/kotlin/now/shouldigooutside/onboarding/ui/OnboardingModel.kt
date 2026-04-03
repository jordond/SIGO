package now.shouldigooutside.onboarding.ui

import androidx.compose.runtime.Stable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.onboarding.ui.navigation.OnboardingDestination

private val destinations = listOfNotNull(
    OnboardingDestination.Welcome,
    OnboardingDestination.Location,
    OnboardingDestination.Preferences,
    OnboardingDestination.Activities,
    OnboardingDestination.Summary,
)

@Stable
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

        @Suppress("USELESS_CAST") // Needed for iOS for some reason
        updateState { it.copy(currentDestination = destination as OnboardingDestination) }
    }

    fun onClick() {
        val current = state.value.currentDestination

        val index = destinations.indexOf(current)
        if (index == destinations.lastIndex) {
            settingsRepo.update { it.copy(hasCompletedOnboarding = true) }
            emit(Event.Finish)
        } else if (index != -1) {
            @Suppress("USELESS_CAST") // Needed for iOS for some reason
            emitNavigation(destinations[index + 1] as OnboardingDestination)
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
