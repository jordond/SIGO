package app.sigot.onboarding.ui.location

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.foundation.ktx.ensureExecutionTime
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.location.LocationResult
import app.sigot.core.resources.Res
import app.sigot.core.resources.location_geolocation_error
import app.sigot.core.resources.location_geolocation_not_allowed
import app.sigot.core.resources.location_geolocation_not_found
import app.sigot.core.resources.location_geolocation_not_supported
import app.sigot.onboarding.ui.location.LocationModel.Event
import app.sigot.onboarding.ui.location.LocationModel.State
import dev.stateholder.extensions.viewmodel.UiStateViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

internal class LocationModel(
    settingsRepo: SettingsRepo,
    private val locationRepo: LocationRepo,
) : UiStateViewModel<State, Event>(
        State(
            permissionStatus = locationRepo.initialPermissionStatus(),
        ),
    ) {
    private var locationJob: Job? = null

    init {
        settingsRepo.settings
            .map { it.lastLocation }
            .distinctUntilChanged()
            .mergeState { state, lastLocation -> state.copy(location = lastLocation) }
    }

    fun requestPermission() {
        viewModelScope.launch {
            val result = locationRepo.requestPermission()
            updateState { it.copy(permissionStatus = result) }
        }
    }

    fun getLocation() {
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch {
            updateState { it.copy(loading = true, locationResult = null) }
            val result = ensureExecutionTime(ENSURED_DURATION) {
                locationRepo.location()
            }

            if (result is LocationResult.Failed) {
                val message = when (result) {
                    is LocationResult.Error -> Res.string.location_geolocation_error
                    is LocationResult.NotAllowed -> Res.string.location_geolocation_not_allowed
                    is LocationResult.NotFound -> Res.string.location_geolocation_not_found
                    is LocationResult.NotSupported -> Res.string.location_geolocation_not_supported
                }

                emit(Event.LocationError(message))
            }

            updateState { state ->
                val permissionStatus = if (result is LocationResult.NotAllowed) {
                    LocationPermissionStatus.Denied(result.permanent)
                } else {
                    state.permissionStatus
                }

                state.copy(loading = false, locationResult = result, permissionStatus = permissionStatus)
            }
        }
    }

    @Stable
    data class State(
        val permissionStatus: LocationPermissionStatus = LocationPermissionStatus.Unknown,
        val loading: Boolean = false,
        val locationResult: LocationResult? = null,
        val canGeolocate: Boolean? = null,
        val location: Location? = null,
    )

    sealed interface Event {
        data class LocationError(
            val error: StringResource,
        ) : Event
    }

    companion object {
        const val ENSURED_DURATION = 3000L
    }
}

private fun LocationRepo.initialPermissionStatus() =
    if (hasPermission()) LocationPermissionStatus.Granted else LocationPermissionStatus.Unknown
