package now.shouldigooutside.forecast.ui.location

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.location.SearchLocationUseCase
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.checkCancellation
import now.shouldigooutside.core.foundation.ktx.ensureExecutionTime
import now.shouldigooutside.core.model.location.Location

private const val MIN_SEARCH_INDICATOR_MS = 500L

@Stable
internal class LocationSearchModel(
    private val settingsRepo: SettingsRepo,
    private val forecastStateHolder: ForecastStateHolder,
    private val searchLocationUseCase: SearchLocationUseCase,
) : StateViewModel<LocationSearchModel.State>(
        State(
            usingCurrentLocation = !settingsRepo.settings.value.useCustomLocation,
        ),
    ) {
    private val logger = Logger.withTag("LocationSearchModel")
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
        viewModelScope.launch {
            settingsRepo.settings.first { !it.useCustomLocation }
            forecastStateHolder.fetch()
        }
    }

    @Immutable
    data class State(
        val usingCurrentLocation: Boolean = true,
        val searchQuery: String = "",
        val searchResults: PersistentList<Location> = persistentListOf(),
        val searching: Boolean = false,
    )
}
