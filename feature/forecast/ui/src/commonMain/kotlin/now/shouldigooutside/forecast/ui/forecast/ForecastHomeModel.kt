package now.shouldigooutside.forecast.ui.forecast

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.stateholder.extensions.viewmodel.StateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.AppStateHolder
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetActivitiesScoreUseCase
import now.shouldigooutside.core.domain.location.SearchLocationUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.checkCancellation
import now.shouldigooutside.core.foundation.ktx.ensureExecutionTime
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.forecast.blockForPeriod
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.scoreForPeriod
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.forecast.ui.forecast.ForecastHomeModel.State

private const val MIN_SEARCH_INDICATOR_MS = 500L

@Stable
internal class ForecastHomeModel(
    private val settingsRepo: SettingsRepo,
    private val forecastStateHolder: ForecastStateHolder,
    private val searchLocationUseCase: SearchLocationUseCase,
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
    private val logger = Logger.withTag("ForecastHomeModel")
    private var searchJob: Job? = null

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

    fun openLocationSheet() {
        updateState { it.copy(showLocationSheet = true) }
    }

    fun closeLocationSheet() {
        updateState { state ->
            state.copy(
                showLocationSheet = false,
                searchQuery = "",
                searchResults = persistentListOf(),
            )
        }
    }

    fun searchLocation(query: String) {
        updateState { it.copy(searchQuery = query, searching = query.isNotBlank()) }
        searchJob?.cancel()
        if (query.isBlank()) {
            updateState { it.copy(searchResults = persistentListOf(), searching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            try {
                val results = ensureExecutionTime(MIN_SEARCH_INDICATOR_MS) {
                    searchLocationUseCase.search(query).toPersistentList()
                }
                updateState { it.copy(searchResults = results, searching = false) }
            } catch (cause: Exception) {
                cause.checkCancellation()
                logger.e(cause) { "Location search failed" }
                updateState { it.copy(searchResults = persistentListOf(), searching = false) }
            }
        }
    }

    fun selectLocation(location: Location) {
        settingsRepo.update { settings ->
            settings.copy(
                customLocation = location,
                useCustomLocation = true,
            )
        }
        closeLocationSheet()
        viewModelScope.launch {
            settingsRepo.settings.first { it.useCustomLocation && it.customLocation == location }
            forecastStateHolder.fetch()
        }
    }

    fun useCurrentLocation() {
        settingsRepo.update { settings ->
            settings.copy(
                customLocation = null,
                useCustomLocation = false,
            )
        }
        closeLocationSheet()
        viewModelScope.launch {
            settingsRepo.settings.first { !it.useCustomLocation }
            forecastStateHolder.fetch()
        }
    }

    data class State(
        val location: Location?,
        val selectedActivity: Activity,
        val units: Units,
        val status: AsyncResult<Forecast>,
        val activityScores: PersistentList<ActivityForecastScore>,
        val period: ForecastPeriod,
        val forecast: Forecast? = null,
        val usingCurrentLocation: Boolean = true,
        val showLocationSheet: Boolean = false,
        val searchQuery: String = "",
        val searchResults: PersistentList<Location> = persistentListOf(),
        val searching: Boolean = false,
        val activities: PersistentList<Activity> = persistentListOf(Activity.General),
    ) {
        val hasMultipleActivities: Boolean get() = activities.size > 1
        val loading: Boolean = status is AsyncResult.Loading
        val refreshing: Boolean = loading && forecast != null
        val currentScore: ActivityForecastScore? =
            activityScores.firstOrNull { it.activity == selectedActivity }
        val currentBlock: ForecastBlock? = forecast?.blockForPeriod(period)
        val currentPeriodScore: Score? = currentScore?.score?.scoreForPeriod(period)
    }
}

private fun state(
    appStateHolder: AppStateHolder,
    settingsRepo: SettingsRepo,
    forecastStateHolder: ForecastStateHolder,
    getActivitiesScoreUseCase: GetActivitiesScoreUseCase,
) = composedStateProvider(
    initialState = State(
        location = settingsRepo.settings.value.run {
            if (useCustomLocation) customLocation else lastLocation
        },
        usingCurrentLocation = !settingsRepo.settings.value.useCustomLocation,
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
            val location = if (settings.useCustomLocation) settings.customLocation else settings.lastLocation
            val activities =
                if (settings.enableActivities) settings.activities.keys else listOf(Activity.General)
            copy(
                location = location,
                selectedActivity = settings.selectedActivity,
                units = settings.units,
                usingCurrentLocation = settings.useCustomLocation,
                activities = activities.toPersistentList(),
            )
        }

    forecastStateHolder.state.into { status ->
        when (status) {
            is AsyncResult.Success -> copy(status = status, forecast = status.data)
            else -> copy(status = status)
        }
    }

    getActivitiesScoreUseCase.scoresFlow() into { scores -> copy(activityScores = scores.toPersistentList()) }
}
