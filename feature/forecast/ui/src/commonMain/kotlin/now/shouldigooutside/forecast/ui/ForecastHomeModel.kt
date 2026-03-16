package now.shouldigooutside.forecast.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.stateholder.extensions.viewmodel.UiStateViewModel
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
import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.domain.location.SearchLocationUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.ensureExecutionTime
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.ForecastData
import now.shouldigooutside.core.model.ForecastPeriodData
import now.shouldigooutside.core.model.errorOrNull
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.location.LocationPermissionStatus
import now.shouldigooutside.core.model.location.LocationPermissionStatus.Denied
import now.shouldigooutside.core.model.location.LocationPermissionStatus.Granted
import now.shouldigooutside.core.model.location.LocationPermissionStatus.Unknown
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_error_generic
import now.shouldigooutside.core.resources.location_geolocation_error
import now.shouldigooutside.core.resources.location_geolocation_not_allowed
import now.shouldigooutside.core.resources.location_geolocation_not_found
import now.shouldigooutside.core.resources.location_geolocation_not_supported
import now.shouldigooutside.forecast.ui.ForecastHomeModel.Event
import now.shouldigooutside.forecast.ui.ForecastHomeModel.State
import org.jetbrains.compose.resources.StringResource
import kotlin.coroutines.cancellation.CancellationException

private const val MIN_SEARCH_INDICATOR_MS = 500L

@Stable
internal class ForecastHomeModel(
    private val settingsRepo: SettingsRepo,
    locationRepo: LocationRepo,
    private val forecastStateHolder: ForecastStateHolder,
    private val searchLocationUseCase: SearchLocationUseCase,
) : UiStateViewModel<State, Event>(
        state(
            settingsRepo = settingsRepo,
            locationRepo = locationRepo,
            forecastStateHolder = forecastStateHolder,
        ),
    ) {
    private val logger = Logger.withTag("ForecastHomeModel")

    init {
        forecastStateHolder.start(viewModelScope)

        viewModelScope.launch {
            state
                .map { it.status.errorOrNull() }
                .distinctUntilChanged()
                .collect { error -> error.handleForecastError() }
        }
    }

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
        updateState {
            it.copy(
                showLocationSheet = false,
                searchQuery = "",
                searchResults = persistentListOf(),
            )
        }
    }

    private var searchJob: Job? = null

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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logger.e(e) { "Location search failed" }
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

    override fun onCleared() {
        forecastStateHolder.stop()
        super.onCleared()
    }

    private fun Throwable?.handleForecastError() {
        if (this == null) return
        val message = when (this) {
            is LocationResult.Failed -> when (this) {
                is LocationResult.Error -> Res.string.location_geolocation_error
                is LocationResult.NotAllowed -> Res.string.location_geolocation_not_allowed
                is LocationResult.NotFound -> Res.string.location_geolocation_not_found
                is LocationResult.NotSupported -> Res.string.location_geolocation_not_supported
            }
            else -> Res.string.forecast_error_generic
        }

        logger.e(this) { "Error getting forecast" }
        emit(Event.Error(message))

        if (this is LocationResult.NotAllowed) {
            updateState { it.copy(permissionStatus = Denied(permanent)) }
        }
    }

    data class State(
        val location: Location?,
        val preferences: Preferences,
        val status: AsyncResult<ForecastData>,
        val period: ForecastPeriod = ForecastPeriod.Now,
        val permissionStatus: LocationPermissionStatus = Unknown,
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

    sealed interface Event {
        data class Error(
            val message: StringResource,
        ) : Event
    }
}

private fun state(
    settingsRepo: SettingsRepo,
    locationRepo: LocationRepo,
    forecastStateHolder: ForecastStateHolder,
) = composedStateProvider(
    initialState = State(
        location = settingsRepo.settings.value.run {
            if (useCustomLocation) customLocation else lastLocation
        },
        usingCurrentLocation = !settingsRepo.settings.value.useCustomLocation,
        preferences = settingsRepo.settings.value.preferences,
        permissionStatus = if (locationRepo.hasPermission()) Granted else Unknown,
        status = forecastStateHolder.state.value,
    ),
) {
    settingsRepo.settings
        .map { settings ->
            val location = if (settings.useCustomLocation) settings.customLocation else settings.lastLocation
            Triple(location, settings.preferences, !settings.useCustomLocation)
        }.distinctUntilChanged()
        .into { (location, preferences, usingCurrent) ->
            copy(location = location, preferences = preferences, usingCurrentLocation = usingCurrent)
        }

    forecastStateHolder.state.into { status ->
        when (status) {
            is AsyncResult.Success -> copy(status = status, forecast = status.data)
            else -> copy(status = status)
        }
    }
}
