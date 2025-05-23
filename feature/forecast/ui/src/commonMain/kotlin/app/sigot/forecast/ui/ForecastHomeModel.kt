package app.sigot.forecast.ui

import androidx.lifecycle.viewModelScope
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.GetScoreUseCase
import app.sigot.core.domain.forecast.convert
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.foundation.ktx.mapDistinct
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.location.LocationPermissionStatus.Denied
import app.sigot.core.model.location.LocationPermissionStatus.Granted
import app.sigot.core.model.location.LocationPermissionStatus.Unknown
import app.sigot.core.model.location.LocationResult
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.score.ForecastScore
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_error_generic
import app.sigot.core.resources.location_geolocation_error
import app.sigot.core.resources.location_geolocation_not_allowed
import app.sigot.core.resources.location_geolocation_not_found
import app.sigot.core.resources.location_geolocation_not_supported
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

internal class ForecastHomeModel(
    settingsRepo: SettingsRepo,
    locationRepo: LocationRepo,
    private val getForecastUseCase: GetForecastUseCase,
    private val getScoresUseCase: GetScoreUseCase,
) : UiStateViewModel<ForecastHomeModel.State, ForecastHomeModel.Event>(
        State(
            location = settingsRepo.settings.value.lastLocation,
            preferences = settingsRepo.settings.value.preferences,
            permissionStatus = if (locationRepo.hasPermission()) Granted else Unknown,
        ),
    ) {
    init {
        settingsRepo.settings
            .map { it.lastLocation to it.preferences }
            .distinctUntilChanged()
            .mergeState { state, (lastLocation, preferences) ->
                state.copy(location = lastLocation, preferences = preferences)
            }

        state
            .mapDistinct { it.forecast }
            .flatMapLatest { forecast ->
                if (forecast == null) {
                    flowOf(null)
                } else {
                    getScoresUseCase.scoreFor(forecast)
                }
            }.mergeState { state, value -> state.copy(score = value) }

        viewModelScope.launch { getForecast() }
    }

    fun forceRefresh() {
        if (state.value.refreshing || state.value.loading) return

        viewModelScope.launch {
            updateState { it.copy(refreshing = true) }
            getForecast()
            updateState { it.copy(refreshing = false) }
        }
    }

    private suspend fun getForecast() {
        if (state.value.loading) return

        updateState { it.copy(loading = true) }
        val result = getForecastUseCase
            .forecastForCurrentLocation()
            .onFailure { it.handleForecastError() }
            .getOrNull()

        updateState { it.copy(loading = false, forecast = result ?: it.forecast) }
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

        emit(Event.Error(message))

        if (this is LocationResult.NotAllowed) {
            updateState { it.copy(permissionStatus = Denied(permanent)) }
        }
    }

    data class State(
        val location: Location?,
        val preferences: Preferences,
        val permissionStatus: LocationPermissionStatus = Unknown,
        val loading: Boolean = false,
        val refreshing: Boolean = false,
        val forecast: Forecast? = null,
        val score: ForecastScore? = null,
    ) {
        val convertedForecast: Forecast? = forecast?.convert(preferences.units)
    }

    sealed interface Event {
        data class Error(
            val message: StringResource,
        ) : Event
    }
}
