package now.shouldigooutside.forecast.ui.forecast.details

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import now.shouldigooutside.core.domain.forecast.ActivityForecastScore
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.mapDistinct
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.forecast.blockForPeriod
import now.shouldigooutside.core.model.forecast.scoreForBlock
import now.shouldigooutside.core.model.getOrNull
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.forecast.ui.navigation.ForecastDetailsRoute

@Stable
internal class ForecastDetailsModel(
    savedStateHandle: SavedStateHandle,
    forecastStateHolder: ForecastStateHolder,
    settingsRepo: SettingsRepo,
    getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
) : StateViewModel<ForecastDetailsModel.State>(
        composedStateProvider(
            State(
                initialPeriod = savedStateHandle.toRoute<ForecastDetailsRoute>().period,
                forecast = forecastStateHolder.state.value.getOrNull(),
                selectedActivity = settingsRepo.settings.value.selectedActivity,
                activityScores = getActivitiesScoreUseCase.scores(),
                loadingForecast = forecastStateHolder.state.value is AsyncResult.Loading,
            ),
        ) {
            forecastStateHolder.state.into { result ->
                val forecast = result.getOrNull()
                val newSelected =
                    if (!hasLoaded && forecast != null) {
                        forecast.blockForPeriod(initialPeriod)
                    } else {
                        selected
                    }
                copy(
                    forecast = forecast,
                    selected = newSelected,
                    hasLoaded = forecast != null,
                    loadingForecast = result is AsyncResult.Loading,
                )
            }

            combine(
                settingsRepo.settings.mapDistinct { it.selectedActivity },
                getActivitiesScoreUseCase.scoresFlow(),
            ) { activity, scores ->
                activity to scores
            }.distinctUntilChanged()
                .into { (activity, scores) ->
                    copy(selectedActivity = activity, activityScores = scores)
                }
        },
    ) {
    fun select(block: ForecastBlock?) {
        updateState { state ->
            val selected = if (block == state.selected) null else block
            state.copy(selected = selected)
        }
    }

    data class State(
        val initialPeriod: ForecastPeriod,
        val forecast: Forecast?,
        val selectedActivity: Activity = Activity.General,
        val activityScores: List<ActivityForecastScore> = emptyList(),
        val selected: ForecastBlock? = null,
        val hasLoaded: Boolean = false,
        val loadingForecast: Boolean = false,
    ) {
        val currentScore: ForecastScore? =
            activityScores.firstOrNull { it.activity == selectedActivity }?.score
        val selectedScore: Score? = selected?.let { block ->
            currentScore?.let { score -> forecast?.scoreForBlock(block, score) }
        }
    }
}
