package now.shouldigooutside.forecast.ui.activities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore

@Stable
internal class ActivitiesModel(
    settingsRepo: SettingsRepo,
    getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
) : StateViewModel<ActivitiesModel.State>(
        composedStateProvider(
            State(
                selected = settingsRepo.settings.value.selectedActivity,
                scores = getActivitiesScoreUseCase.scores().toPersistentList(),
            ),
        ) {
            settingsRepo.settings into { value -> copy(selected = value.selectedActivity) }
            getActivitiesScoreUseCase.scoresFlow() into { value -> copy(scores = value.toPersistentList()) }
        },
    ) {
    fun update(period: ForecastPeriod) {
        updateState { it.copy(period = period) }
    }

    @Immutable
    data class State(
        val selected: Activity,
        val scores: PersistentList<ActivityForecastScore>,
        val period: ForecastPeriod = ForecastPeriod.Now,
    )
}
