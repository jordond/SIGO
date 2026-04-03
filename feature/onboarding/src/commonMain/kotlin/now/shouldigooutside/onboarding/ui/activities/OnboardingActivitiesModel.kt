package now.shouldigooutside.onboarding.ui.activities

import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences

@Stable
internal class OnboardingActivitiesModel(
    private val settingsRepo: SettingsRepo,
) : StateViewModel<OnboardingActivitiesModel.State>(
        State(
            selectedActivities = settingsRepo.settings.value.activities.keys
                .toPersistentSet(),
        ),
    ) {
    init {
        settingsRepo.settings
            .map { it.activities.keys }
            .distinctUntilChanged()
            .mergeState { state, keys -> state.copy(selectedActivities = keys.toPersistentSet()) }
    }

    fun toggleActivity(activity: Activity) {
        if (settingsRepo.settings.value.activities
                .containsKey(activity)
        ) {
            settingsRepo.update { it.remove(activity) }
        } else {
            settingsRepo.update { it.add(activity, Preferences.defaultFor(activity)) }
        }
    }

    data class State(
        val selectedActivities: PersistentSet<Activity>,
    ) {
        val availableActivities: PersistentList<Activity> = Activity.all
            .filterNot { it is Activity.General }
            .toPersistentList()
    }
}
