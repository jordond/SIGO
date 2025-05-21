package app.sigot.onboarding.ui.location

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.resources.Res
import app.sigot.core.resources.location_geolocation_error
import app.sigot.core.resources.location_geolocation_not_found
import app.sigot.onboarding.ui.location.LocationModel.Event
import app.sigot.onboarding.ui.location.LocationModel.State
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.TrackingStatus
import dev.jordond.compass.geolocation.isPermissionDenied
import dev.jordond.compass.geolocation.isPermissionDeniedForever
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
    private var trackingJob: Job? = null

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

    fun startTracking() {
        if (trackingJob?.isActive == true) return

        trackingJob = locationRepo.track().mergeState { state, value ->
            if (value is TrackingStatus.Error) {
                val message = when (value.cause) {
                    is GeolocatorResult.GeolocationFailed -> Res.string.location_geolocation_error
                    is GeolocatorResult.NotFound -> Res.string.location_geolocation_not_found
                    else -> null
                }

                if (message != null) {
                    emit(Event.LocationError(message))
                }
            }
            state.updateTrackingStatus(value)
        }
    }

    override fun onCleared() {
        trackingJob?.cancel()
        super.onCleared()
    }

    private fun State.updateTrackingStatus(status: TrackingStatus): State {
        val permissionStatus = when {
            status is TrackingStatus.Error && status.cause.isPermissionDenied() -> {
                LocationPermissionStatus.Denied(status.cause.isPermissionDeniedForever())
            }
            status is TrackingStatus.Tracking || status is TrackingStatus.Update -> {
                LocationPermissionStatus.Granted
            }
            else -> {
                permissionStatus
            }
        }

        return copy(permissionStatus = permissionStatus, trackingStatus = status)
    }

    @Stable
    data class State(
        val permissionStatus: LocationPermissionStatus = LocationPermissionStatus.Unknown,
        val trackingStatus: TrackingStatus = TrackingStatus.Idle,
        val canGeolocate: Boolean? = null,
        val location: Location? = null,
    ) {
        val isTracking: Boolean = trackingStatus.isActive
    }

    sealed interface Event {
        data class LocationError(
            val error: StringResource,
        ) : Event
    }
}

private fun LocationRepo.initialPermissionStatus() =
    if (hasPermission()) LocationPermissionStatus.Granted else LocationPermissionStatus.Unknown
