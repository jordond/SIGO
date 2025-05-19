package app.sigot.onboarding.ui.location

import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.forecast.Location
import app.sigot.onboarding.ui.location.LocationModel.Event
import app.sigot.onboarding.ui.location.LocationModel.State
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class LocationModel(
    private val settingsRepo: SettingsRepo,
) : UiStateViewModel<State, Event>(State(settingsRepo.settings.value.preferences.enableLocationUpdates)) {
    init {
        settingsRepo.settings
            .map { it.preferences.enableLocationUpdates to it.lastLocation }
            .distinctUntilChanged()
            .mergeState { state, (locationUpdates, lastLocation) ->
                state.copy(enableLocationUpdates = locationUpdates, location = lastLocation)
            }
    }

    fun toggleLocationUpdates() {
        settingsRepo.update { settings ->
            val preferences = settings.preferences.copy(
                enableLocationUpdates = !settings.preferences.enableLocationUpdates,
            )
            settings.updatePreferences(preferences)
        }
    }

    fun getLocation() {
        // TODO: Use Compass to get the location, probably a repo
    }

    data class State(
        val enableLocationUpdates: Boolean,
        val location: Location? = null,
    )

    sealed interface Event
}
