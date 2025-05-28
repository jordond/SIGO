package app.sigot.forecast.ui

import androidx.lifecycle.viewModelScope
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.domain.forecast.convert
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.ForecastData
import app.sigot.core.model.ForecastPeriodData
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
import co.touchlab.kermit.Logger
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

internal class ForecastHomeModel(
    settingsRepo: SettingsRepo,
    locationRepo: LocationRepo,
    private val getForecastUseCase: GetForecastUseCase,
    private val scoreCalculator: ScoreCalculator,
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
                val data = state.forecast?.copy(
                    forecast = state.forecast.forecast.convert(preferences.units),
                    score = scoreCalculator.calculate(state.forecast.forecast, preferences),
                )

                state.copy(location = lastLocation, preferences = preferences, forecast = data)
            }

        viewModelScope.launch { getForecast() }
    }

    fun updatePeriod(period: ForecastPeriod) {
        updateState { it.copy(period = period) }
    }

    fun forceRefresh() {
        viewModelScope.launch {
            getForecast()
        }
    }

    private suspend fun getForecast() {
        if (state.value.loading) return

        updateState { it.copy(loading = true) }
        val prefs = state.value.preferences
        val forecast = getForecastUseCase
            .forecastForCurrentLocation(prefs.units)
            .onFailure { it.handleForecastError() }
            .getOrNull()

        val score = if (forecast == null) null else scoreCalculator.calculate(forecast, prefs)

        updateState { state ->
            val data =
                if (forecast != null && score != null) ForecastData(forecast, score) else state.forecast

            state.copy(loading = false, forecast = data)
        }
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

        Logger.e(this) { "Error getting forecast" }
        emit(Event.Error(message))

        if (this is LocationResult.NotAllowed) {
            updateState { it.copy(permissionStatus = Denied(permanent)) }
        }
    }

    data class State(
        val location: Location?,
        val preferences: Preferences,
        val period: ForecastPeriod = ForecastPeriod.Today,
        val permissionStatus: LocationPermissionStatus = Unknown,
        val loading: Boolean = false,
        val forecast: ForecastData? = null,
    ) {
        val refreshing: Boolean = loading && forecast != null
        val data: ForecastPeriodData? = forecast?.forPeriod(period)
    }

    sealed interface Event {
        data class Error(
            val message: StringResource,
        ) : Event
    }
}
