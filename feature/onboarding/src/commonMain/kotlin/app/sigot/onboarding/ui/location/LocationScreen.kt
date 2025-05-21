package app.sigot.onboarding.ui.location

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.location.LocationResult
import app.sigot.core.resources.Res
import app.sigot.core.resources.onboarding_location
import app.sigot.core.resources.onboarding_location_subtext
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.autoSize
import app.sigot.core.ui.components.snackbar.LocalSnackbarProvider
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.PreviewData
import app.sigot.onboarding.ui.OnboardingScreen
import app.sigot.onboarding.ui.location.LocationScreenAction.RequestPermission
import app.sigot.onboarding.ui.location.LocationScreenAction.StartTracking
import app.sigot.onboarding.ui.location.components.LocationDetailsCard
import app.sigot.onboarding.ui.location.components.LocationPermissionStatusCard
import app.sigot.onboarding.ui.location.components.LocationServicesCard
import app.sigot.onboarding.ui.navigation.OnboardingDestination
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

internal sealed interface LocationScreenAction {
    data object RequestPermission : LocationScreenAction

    data object StartTracking : LocationScreenAction
}

@Composable
internal fun LocationScreen(model: LocationModel = koinViewModel()) {
    val state by model.collectAsState()

    val snackbar = LocalSnackbarProvider.current
    HandleEvents(model) { event ->
        when (event) {
            is LocationModel.Event.LocationError -> snackbar.show(event.error)
        }
    }

    LocationScreen(
        location = state.location,
        isLoading = state.loading,
        locationResult = state.locationResult,
        permissionStatus = state.permissionStatus,
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is RequestPermission -> model.requestPermission()
                is StartTracking -> model.getLocation()
            }
        },
    )
}

@Composable
internal fun LocationScreen(
    location: Location?,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    locationResult: LocationResult? = null,
    dispatcher: Dispatcher<LocationScreenAction> = rememberDispatcher {},
    locationServicesAvailable: Boolean = true,
    permissionStatus: LocationPermissionStatus = LocationPermissionStatus.Unknown,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(
                    text = Res.string.onboarding_location,
                    style = AppTheme.typography.header,
                    autoSize = AppTheme.typography.header.autoSize(),
                    maxLines = 1,
                )

                Text(
                    text = Res.string.onboarding_location_subtext,
                    modifier = Modifier.padding(start = 8.dp),
                    style = AppTheme.typography.body1,
                )
            }

            LocationServicesCard(
                locationServicesAvailable = locationServicesAvailable,
            )

            Crossfade(locationServicesAvailable) { available ->
                if (available) {
                    LocationPermissionStatusCard(
                        permissionStatus = permissionStatus,
                        onClick = dispatcher.rememberRelay(RequestPermission),
                    )
                }
            }

            AnimatedVisibility(
                visible = location != null || permissionStatus is LocationPermissionStatus.Granted,
            ) {
                LocationDetailsCard(
                    location = location,
                    isLoading = isLoading,
                    status = locationResult,
                    getLocation = dispatcher.rememberRelay(StartTracking),
                )
            }
        }
    }
}

@Preview
@Composable
private fun LocationScreenPreview() {
    AppPreview {
        OnboardingScreen(OnboardingDestination.Location) {
            LocationScreen(
                location = PreviewData.location,
                locationResult = LocationResult.Success(PreviewData.location),
                permissionStatus = LocationPermissionStatus.Granted,
            )
        }
    }
}
