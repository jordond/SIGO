package now.shouldigooutside.forecast.ui.activities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences

@Stable
internal class ActivitiesModel(
    settingsRepo: SettingsRepo,
) : StateViewModel<ActivitiesModel.State>(
        composedStateProvider(
            State(
                activities = settingsRepo.settings.value.activities
                    .toPersistentMap(),
            ),
        ) {
            settingsRepo.settings into { value -> copy(activities = value.activities.toPersistentMap()) }
        },
    ) {
    @Immutable
    data class State(
        val activities: PersistentMap<Activity, Preferences>,
    )
}
