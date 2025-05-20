package app.sigot.onboarding.ui.location

import androidx.compose.runtime.Stable
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
import org.jetbrains.compose.resources.StringResource

internal class LocationModel(
    private val settingsRepo: SettingsRepo,
    private val locationRepo: LocationRepo,
) : UiStateViewModel<State, Event>(
        State(hasLocationPermission = locationRepo.hasPermission()),
    ) {
    private var trackingJob: Job? = null

    init {
        settingsRepo.settings
            .map { it.lastLocation }
            .distinctUntilChanged()
            .mergeState { state, lastLocation -> state.copy(location = lastLocation) }
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
            state.copy(trackingStatus = value)
        }
    }

    override fun onCleared() {
        trackingJob?.cancel()
        super.onCleared()
    }

    @Stable
    data class State(
        val hasLocationPermission: Boolean,
        val trackingStatus: TrackingStatus = TrackingStatus.Idle,
        val canGeolocate: Boolean? = null,
        val location: Location? = null,
    ) {
        val isTracking: Boolean = trackingStatus.isActive
        val permissionStatus: LocationPermissionStatus =
            if (trackingStatus is TrackingStatus.Error && trackingStatus.cause.isPermissionDenied()) {
                LocationPermissionStatus.Denied(trackingStatus.cause.isPermissionDeniedForever())
            } else {
                if (hasLocationPermission) {
                    LocationPermissionStatus.Granted
                } else {
                    LocationPermissionStatus.Unknown
                }
            }
    }

    sealed interface Event {
        data class LocationError(
            val error: StringResource,
        ) : Event
    }
}
