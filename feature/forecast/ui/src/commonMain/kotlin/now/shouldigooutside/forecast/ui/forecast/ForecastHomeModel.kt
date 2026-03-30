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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.location.SearchLocationUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.checkCancellation
import now.shouldigooutside.core.foundation.ktx.ensureExecutionTime
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.ForecastData
import now.shouldigooutside.core.model.ForecastPeriodData
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.forecast.ui.forecast.ForecastHomeModel.State

private const val MIN_SEARCH_INDICATOR_MS = 500L

@Stable
internal class ForecastHomeModel(
    private val settingsRepo: SettingsRepo,
    private val forecastStateHolder: ForecastStateHolder,
    private val searchLocationUseCase: SearchLocationUseCase,
) : StateViewModel<State>(
        state(
            settingsRepo = settingsRepo,
            forecastStateHolder = forecastStateHolder,
        ),
    ) {
    private val logger = Logger.withTag("ForecastHomeModel")
    private var searchJob: Job? = null

    fun updatePeriod(period: ForecastPeriod) {
        updateState { it.copy(period = period) }
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
        val preferences: Preferences,
        val units: Units,
        val status: AsyncResult<ForecastData>,
        val period: ForecastPeriod = ForecastPeriod.Now,
        val forecast: ForecastData? = null,
        val usingCurrentLocation: Boolean = true,
        val showLocationSheet: Boolean = false,
        val searchQuery: String = "",
        val searchResults: PersistentList<Location> = persistentListOf(),
        val searching: Boolean = false,
    ) {
        val loading: Boolean = status is AsyncResult.Loading
        val refreshing: Boolean = loading && forecast != null
        val data: ForecastPeriodData? = forecast?.forPeriod(period)
    }
}

private data class SettingsSnapshot(
    val location: Location?,
    val preferences: Preferences,
    val units: Units,
    val usingCurrentLocation: Boolean,
)

private fun state(
    settingsRepo: SettingsRepo,
    forecastStateHolder: ForecastStateHolder,
) = composedStateProvider(
    initialState = State(
        location = settingsRepo.settings.value.run {
            if (useCustomLocation) customLocation else lastLocation
        },
        usingCurrentLocation = !settingsRepo.settings.value.useCustomLocation,
        preferences = settingsRepo.settings.value.preferences,
        units = settingsRepo.settings.value.units,
        status = forecastStateHolder.state.value,
    ),
) {
    settingsRepo.settings
        .map { settings ->
            val location = if (settings.useCustomLocation) settings.customLocation else settings.lastLocation
            SettingsSnapshot(location, settings.preferences, settings.units, !settings.useCustomLocation)
        }.distinctUntilChanged()
        .into { snapshot ->
            copy(
                location = snapshot.location,
                preferences = snapshot.preferences,
                units = snapshot.units,
                usingCurrentLocation = snapshot.usingCurrentLocation,
            )
        }

    forecastStateHolder.state.into { status ->
        when (status) {
            is AsyncResult.Success -> copy(status = status, forecast = status.data)
            else -> copy(status = status)
        }
    }
}
