package now.shouldigooutside.forecast.ui.activities.add

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.collections.immutable.PersistentList
import now.shouldigooutside.core.domain.GetPreferenceRangesUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.PreferenceRanges
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.preferences.remainingActivities

@Stable
internal class AddActivityModel(
    private val settingsRepo: SettingsRepo,
    getPreferenceRangesUseCase: GetPreferenceRangesUseCase,
) : UiStateViewModel<AddActivityModel.State, AddActivityModel.Event>(
        State(
            activities = settingsRepo.settings.value.activities
                .remainingActivities(),
            ranges = getPreferenceRangesUseCase.ranges(),
        ),
    ) {
    init {
        getPreferenceRangesUseCase.ranges.mergeState { state, ranges -> state.copy(ranges = ranges) }
    }

    fun select(activity: Activity) {
        updateState { state ->
            state.copy(activity = if (state.activity == activity) null else activity)
        }
    }

    fun updatePreferences(preferences: Preferences) {
        updateState { state ->
            state.copy(editedPreferences = preferences, hasEditedPreferences = true)
        }
    }

    fun resetPreferences() {
        updateState { state ->
            state.copy(editedPreferences = Preferences.default, hasEditedPreferences = false)
        }
    }

    fun save() {
        val activity = state.value.activity ?: return
        settingsRepo.update { settings ->
            settings.add(activity, state.value.preferences)
        }

        emit(Event.Finished)
    }

    @Immutable
    data class State(
        val activities: PersistentList<Activity>,
        val ranges: PreferenceRanges,
        val activity: Activity? = null,
        val editedPreferences: Preferences = Preferences.default,
        val hasEditedPreferences: Boolean = false,
    ) {
        private val defaultPreferences: Preferences =
            activity?.let { Preferences.defaultFor(it) } ?: Preferences.default

        val preferences: Preferences = if (hasEditedPreferences) editedPreferences else defaultPreferences
    }

    sealed interface Event {
        data object Finished : Event
    }
}
