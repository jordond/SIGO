package now.shouldigooutside.forecast.ui.activities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.domain.AppStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore

@Stable
internal class ActivitiesModel(
    private val appStateHolder: AppStateHolder,
    settingsRepo: SettingsRepo,
    getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
) : StateViewModel<ActivitiesModel.State>(
        composedStateProvider(
            State(
                period = appStateHolder.state.value.period,
                selected = settingsRepo.settings.value.selectedActivity,
                scores = getActivitiesScoreUseCase.scores().toState(),
            ),
        ) {
            appStateHolder into { value -> copy(period = value.period) }
            settingsRepo.settings into { value -> copy(selected = value.selectedActivity) }
            getActivitiesScoreUseCase.scoresFlow() into { value -> copy(scores = value.toState()) }
        },
    ) {
    fun update(period: ForecastPeriod) {
        appStateHolder.update(period)
    }

    @Immutable
    data class State(
        val period: ForecastPeriod,
        val selected: Activity,
        val scores: PersistentList<ActivityForecastScore>,
    )
}

private fun List<ActivityForecastScore>.toState(): PersistentList<ActivityForecastScore> {
    if (size == 1 && this[0].activity is Activity.General) return persistentListOf()
    return this.toPersistentList()
}
