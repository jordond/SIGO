package now.shouldigooutside.forecast.ui.activities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import now.shouldigooutside.core.domain.forecast.ActivityForecastScore
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences

@Stable
internal class ActivitiesModel(
    settingsRepo: SettingsRepo,
    getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
) : StateViewModel<ActivitiesModel.State>(
        composedStateProvider(
            State(
                activities = settingsRepo.settings.value.activities
                    .toPersistentMap(),
                scores = getActivitiesScoreUseCase.scores().toPersistentList(),
            ),
        ) {
            settingsRepo.settings into { value -> copy(activities = value.activities.toPersistentMap()) }
            getActivitiesScoreUseCase.scoresFlow() into { value -> copy(scores = value.toPersistentList()) }
        },
    ) {
    @Immutable
    data class State(
        val activities: PersistentMap<Activity, Preferences>,
        val scores: PersistentList<ActivityForecastScore>,
    )
}
