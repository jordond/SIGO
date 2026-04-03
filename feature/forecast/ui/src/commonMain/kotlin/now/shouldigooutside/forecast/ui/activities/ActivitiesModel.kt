package now.shouldigooutside.forecast.ui.activities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.domain.AppStateHolder
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.getOrNull
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.units.Units

@Stable
internal class ActivitiesModel(
    private val appStateHolder: AppStateHolder,
    private val settingsRepo: SettingsRepo,
    forecastStateHolder: ForecastStateHolder,
    getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
) : UiStateViewModel<ActivitiesModel.State, ActivitiesModel.Event>(
        composedStateProvider(
            State(
                period = appStateHolder.state.value.period,
                selected = settingsRepo.settings.value.selectedActivity,
                scores = getActivitiesScoreUseCase.scores().toState(),
                forecast = forecastStateHolder.state.value.getOrNull(),
                location = settingsRepo.settings.value.location,
                units = settingsRepo.settings.value.units,
            ),
        ) {
            appStateHolder into { value -> copy(period = value.period) }
            settingsRepo.settings into { value ->
                copy(
                    selected = value.selectedActivity,
                    location = value.location,
                    units = value.units,
                )
            }
            getActivitiesScoreUseCase.scoresFlow() into { value -> copy(scores = value.toState()) }
            forecastStateHolder into { value -> copy(forecast = value?.getOrNull()) }
        },
    ) {
    fun update(period: ForecastPeriod) {
        appStateHolder.update(period)
    }

    fun activityCardClick(activity: Activity) {
        settingsRepo.update { settings ->
            settings.copy(selectedActivity = activity)
        }
        emit(Event.ToHome)
    }

    @Immutable
    data class State(
        val period: ForecastPeriod,
        val selected: Activity,
        val scores: PersistentList<ActivityForecastScore>,
        val forecast: Forecast? = null,
        val location: Location? = null,
        val units: Units = Units.Metric,
    ) {
        val canAddMore: Boolean =
            scores.size < Activity.all.size - 1 ||
                (scores.size == 1 && scores[0].activity is Activity.General)
    }

    sealed interface Event {
        data object ToHome : Event
    }
}

private fun List<ActivityForecastScore>.toState(): PersistentList<ActivityForecastScore> {
    if (size == 1 && this[0].activity is Activity.General) return persistentListOf()
    return this.toPersistentList()
}
