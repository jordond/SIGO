package app.sigot.forecast.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.AsyncResult
import app.sigot.core.model.ForecastData
import app.sigot.core.model.ForecastPeriodData
import app.sigot.core.model.errorOrNull
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.location.LocationPermissionStatus.Denied
import app.sigot.core.model.location.LocationPermissionStatus.Granted
import app.sigot.core.model.location.LocationPermissionStatus.Unknown
import app.sigot.core.model.location.LocationResult
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_error_generic
import app.sigot.core.resources.location_geolocation_error
import app.sigot.core.resources.location_geolocation_not_allowed
import app.sigot.core.resources.location_geolocation_not_found
import app.sigot.core.resources.location_geolocation_not_supported
import app.sigot.forecast.ui.ForecastHomeModel.Event
import app.sigot.forecast.ui.ForecastHomeModel.State
import co.touchlab.kermit.Logger
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import dev.stateholder.provider.composedStateProvider
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

@Stable
internal class ForecastHomeModel(
    settingsRepo: SettingsRepo,
    locationRepo: LocationRepo,
    private val forecastStateHolder: ForecastStateHolder,
) : UiStateViewModel<State, Event>(
        state(
            settingsRepo = settingsRepo,
            locationRepo = locationRepo,
            forecastStateHolder = forecastStateHolder,
        ),
    ) {
    private val logger = Logger.withTag("ForecastHomeModel")

    init {
        forecastStateHolder.start()

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
        location = settingsRepo.settings.value.lastLocation,
        preferences = settingsRepo.settings.value.preferences,
        permissionStatus = if (locationRepo.hasPermission()) Granted else Unknown,
        status = forecastStateHolder.state.value,
    ),
) {
    settingsRepo.settings
        .map { it.lastLocation to it.preferences }
        .distinctUntilChanged()
        .into { (lastLocation, preferences) -> copy(location = lastLocation, preferences = preferences) }

    forecastStateHolder.state.into { status ->
        when (status) {
            is AsyncResult.Success -> copy(status = status, forecast = status.data)
            else -> copy(status = status)
        }
    }
}
