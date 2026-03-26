package now.shouldigooutside.settings.ui.preferences

import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.collections.immutable.PersistentMap
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import now.shouldigooutside.core.domain.GetPreferenceRangesUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.PreferenceRanges
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units

@Stable
internal class PreferencesModel(
    private val settingsRepo: SettingsRepo,
    getPreferenceRangesUseCase: GetPreferenceRangesUseCase,
) : StateViewModel<PreferencesModel.State>(
        State(
            activities = settingsRepo.settings.value.activities,
            selected = settingsRepo.settings.value.selectedActivity,
            units = settingsRepo.settings.value.units,
            ranges = getPreferenceRangesUseCase.ranges(),
        ),
    ) {
    init {
        settingsRepo.settings
            .map { Triple(it.activities, it.selectedActivity, it.units) }
            .distinctUntilChanged()
            .mergeState { state, (activities, selected, units) ->
                state.copy(activities = activities, selected = selected, units = units)
            }

        getPreferenceRangesUseCase.ranges.mergeState { state, ranges -> state.copy(ranges = ranges) }
    }

    fun selectActivity(activity: Activity) {
        settingsRepo.update { settings ->
            settings.copy(selectedActivity = activity)
        }
    }

    fun update(preferences: Preferences) {
        settingsRepo.update { settings ->
            settings.updatePreferences(state.value.selected, preferences)
        }
    }

    fun deleteSelectedActivity() {
        settingsRepo.update { settings ->
            settings.remove(state.value.selected)
        }
    }

    fun resetSelectedPreferences() {
        val defaults = Preferences.defaultFor(state.value.selected)
        settingsRepo.update { settings ->
            settings.updatePreferences(state.value.selected, defaults)
        }
    }

    data class State(
        val activities: PersistentMap<Activity, Preferences>,
        val selected: Activity,
        val units: Units,
        val ranges: PreferenceRanges,
    ) {
        val tempRange = ranges.temperature
        val maxWindSpeed = ranges.maxWindSpeed
        val preferences: Preferences = activities[selected] ?: Preferences.defaultFor(selected)
    }
}
