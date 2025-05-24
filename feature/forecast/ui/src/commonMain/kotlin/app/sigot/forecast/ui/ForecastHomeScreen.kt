package app.sigot.forecast.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.snackbar.Snackbar
import app.sigot.core.ui.components.snackbar.SnackbarHost
import app.sigot.core.ui.components.snackbar.SnackbarHostState
import app.sigot.core.ui.components.snackbar.rememberSnackbarProvider
import app.sigot.core.ui.preview.AppPreview
import app.sigot.forecast.ui.ForecastHomeModel.Event
import app.sigot.forecast.ui.components.HeaderText
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import kotlinx.datetime.Clock
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ForecastHomeScreen(model: ForecastHomeModel = koinViewModel()) {
    val snackbar = rememberSnackbarProvider()
    HandleEvents(model) { event ->
        when (event) {
            is Event.Error -> snackbar.error(event.message)
        }
    }

    val state by model.collectAsState()

    ForecastHomeScreen(
        location = state.location,
        preferences = state.preferences,
        forecast = state.forecast,
        period = state.period,
        loading = state.loading,
        refreshing = state.refreshing,
        permissionStatus = state.permissionStatus,
        snackbarHostState = snackbar.hostState,
    )
}

@Composable
internal fun ForecastHomeScreen(
    location: Location?,
    preferences: Preferences,
    forecast: ForecastData?,
    modifier: Modifier = Modifier,
    period: ForecastPeriod = ForecastPeriod.Today,
    loading: Boolean = false,
    refreshing: Boolean = false,
    permissionStatus: LocationPermissionStatus = LocationPermissionStatus.Unknown,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val scoreData = remember(forecast, period) {
        forecast?.forPeriod(period)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data -> Snackbar(snackbarData = data) },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
        ) {
            HeaderText(
                instant = forecast?.forecast?.instant ?: Clock.System.now(),
                period = period,
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun ForecastHomeScreenPreview() {
    AppPreview {
        ForecastHomeScreen(
            location = null,
            forecast = null,
            preferences = Preferences.default,
        )
    }
}
