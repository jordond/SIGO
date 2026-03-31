package now.shouldigooutside.ui.home

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.foundation.ktx.mapDistinct
import now.shouldigooutside.core.model.AsyncResult
import now.shouldigooutside.core.model.errorOrNull
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.LocationPermissionStatus
import now.shouldigooutside.core.model.location.LocationPermissionStatus.Denied
import now.shouldigooutside.core.model.location.LocationPermissionStatus.Granted
import now.shouldigooutside.core.model.location.LocationPermissionStatus.Unknown
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_error_generic
import now.shouldigooutside.core.resources.location_geolocation_error
import now.shouldigooutside.core.resources.location_geolocation_not_allowed
import now.shouldigooutside.core.resources.location_geolocation_not_found
import now.shouldigooutside.core.resources.location_geolocation_not_supported
import now.shouldigooutside.ui.home.navigation.HomeTab
import org.jetbrains.compose.resources.StringResource

@Stable
internal class HomeModel(
    private val settingsRepo: SettingsRepo,
    private val forecastStateHolder: ForecastStateHolder,
    locationRepo: LocationRepo,
) : UiStateViewModel<HomeModel.State, HomeModel.Event>(
        State(
            status = forecastStateHolder.state.value,
            permissionStatus = if (locationRepo.hasPermission()) Granted else Unknown,
        ),
    ) {
    private val logger = Logger.withTag("AppHostModel")

    init {
        forecastStateHolder.start(viewModelScope)

        viewModelScope.launch {
            settingsRepo.settings.mapDistinct { it.enableActivities }.collect { enableActivities ->
                if (!enableActivities && state.value.selectedTab == HomeTab.Activities) {
                    updateState { it.copy(selectedTab = HomeTab.Home) }
                    emit(Event.Navigate(HomeTab.Home))
                }
            }
        }

        viewModelScope.launch {
            state
                .map { it.status.errorOrNull() }
                .distinctUntilChanged()
                .collect { error -> error.handleForecastError() }
        }
    }

    fun updateSelectedTab(tab: HomeTab) {
        updateState { it.copy(selectedTab = tab) }
        emit(Event.Navigate(tab))
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

    @Immutable
    data class State(
        val status: AsyncResult<Forecast>,
        val permissionStatus: LocationPermissionStatus,
        val selectedTab: HomeTab = HomeTab.default,
    )

    @Immutable
    sealed interface Event {
        data class Navigate(
            val tab: HomeTab,
        ) : Event

        data class Error(
            val message: StringResource,
        ) : Event
    }
}
