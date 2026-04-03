package now.shouldigooutside.onboarding.ui.location

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.location.LocationPermissionStatus
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.info
import now.shouldigooutside.core.resources.onboarding_location
import now.shouldigooutside.core.resources.onboarding_location_disclaimer
import now.shouldigooutside.core.resources.onboarding_location_subtext
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.components.snackbar.LocalSnackbarProvider
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Info
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.onboarding.ui.OnboardingScreen
import now.shouldigooutside.onboarding.ui.location.LocationScreenAction.RequestPermission
import now.shouldigooutside.onboarding.ui.location.LocationScreenAction.StartTracking
import now.shouldigooutside.onboarding.ui.location.components.LocationDetailsCard
import now.shouldigooutside.onboarding.ui.location.components.LocationPermissionStatusCard
import now.shouldigooutside.onboarding.ui.location.components.LocationServicesCard
import now.shouldigooutside.onboarding.ui.navigation.OnboardingDestination
import org.koin.compose.viewmodel.koinViewModel

@Immutable
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

            Card(
                colors = CardDefaults.disclaimerColors,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
                    modifier = Modifier.padding(12.dp),
                ) {
                    Icon(
                        icon = AppIcons.Lucide.Info,
                        contentDescription = Res.string.info.get(),
                    )

                    Text(
                        text = Res.string.onboarding_location_disclaimer,
                        style = AppTheme.typography.body3,
                        modifier = Modifier.weight(1f),
                    )
                }
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
