package now.shouldigooutside.forecast.ui.forecast

import androidx.compose.runtime.Stable
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.domain.AppStateHolder
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Alert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.forecast.WeatherWindow
import now.shouldigooutside.core.model.forecast.blockForPeriod
import now.shouldigooutside.core.model.forecast.goodWeatherWindows
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.scoreForPeriod
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.forecast.ui.forecast.ForecastHomeModel.State

@Stable
internal class ForecastHomeModel(
    private val settingsRepo: SettingsRepo,
    private val forecastStateHolder: ForecastStateHolder,
    private val appStateHolder: AppStateHolder,
    getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
) : StateViewModel<State>(
        state(
            settingsRepo = settingsRepo,
            forecastStateHolder = forecastStateHolder,
            getActivitiesScoreUseCase = getActivitiesScoreUseCase,
            appStateHolder = appStateHolder,
        ),
    ) {
    fun update(period: ForecastPeriod) {
        appStateHolder.update(period)
    }

    fun update(activity: Activity) {
        settingsRepo.update { settings ->
            settings.copy(selectedActivity = activity)
        }
    }

    fun fetch() {
        forecastStateHolder.fetch()
    }

    data class State(
        val location: Location?,
        val selectedActivity: Activity,
        val units: Units,
        val status: AsyncResult<Forecast>,
        val activityScores: PersistentList<ActivityForecastScore>,
        val period: ForecastPeriod,
        val forecast: Forecast? = null,
        val activities: PersistentList<Activity> = persistentListOf(Activity.General),
        val goodWindow: WeatherWindow? = null,
    ) {
        val hasMultipleActivities: Boolean get() = activities.size > 1
        val loading: Boolean = status is AsyncResult.Loading
        val refreshing: Boolean = loading && forecast != null
        val currentScore: ActivityForecastScore? =
            activityScores.firstOrNull { it.activity == selectedActivity }
        val currentBlock: ForecastBlock? = forecast?.blockForPeriod(period)
        val currentPeriodScore: Score? = currentScore?.score?.scoreForPeriod(period)
        val alerts: List<Alert> = forecast?.alerts.orEmpty()
    }
}

private fun State.withGoodWindow(): State =
    copy(goodWindow = forecast?.goodWeatherWindows(currentScore?.score)?.firstOrNull())

private fun state(
    appStateHolder: AppStateHolder,
    settingsRepo: SettingsRepo,
    forecastStateHolder: ForecastStateHolder,
    getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
) = composedStateProvider(
    initialState = State(
        location = settingsRepo.settings.value.location,
        selectedActivity = settingsRepo.settings.value.selectedActivity,
        units = settingsRepo.settings.value.units,
        status = forecastStateHolder.state.value,
        activityScores = getActivitiesScoreUseCase.scores().toPersistentList(),
        period = appStateHolder.state.value.period,
    ),
) {
    appStateHolder into { copy(period = it.period) }

    settingsRepo.settings
        .into { settings ->
            val location = settings.location
            val activities =
                if (settings.enableActivities) settings.activities.keys else listOf(Activity.General)
            copy(
                location = location,
                selectedActivity = settings.selectedActivity,
                units = settings.units,
                activities = activities.toPersistentList(),
            ).withGoodWindow()
        }

    forecastStateHolder.state.into { status ->
        when (status) {
            is AsyncResult.Success -> copy(status = status, forecast = status.data).withGoodWindow()
            else -> copy(status = status)
        }
    }

    getActivitiesScoreUseCase.scoresFlow() into { scores ->
        copy(activityScores = scores.toPersistentList()).withGoodWindow()
    }
}
