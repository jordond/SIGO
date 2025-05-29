package app.sigot.forecast.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.snackbar.Snackbar
import app.sigot.core.ui.components.snackbar.SnackbarHost
import app.sigot.core.ui.components.snackbar.SnackbarHostState
import app.sigot.core.ui.components.snackbar.rememberSnackbarProvider
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.PreviewData
import app.sigot.forecast.ui.ForecastHomeModel.Event
import app.sigot.forecast.ui.components.Header
import app.sigot.forecast.ui.components.mappers.rememberInstant
import app.sigot.forecast.ui.section.ForecastScoreContent
import app.sigot.forecast.ui.section.HomeBottomBar
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.dispatcher.rememberRelayOf
import dev.stateholder.extensions.HandleEvents
import dev.stateholder.extensions.collectAsState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ForecastHomeScreen(
    toViewDetails: () -> Unit,
    toPreferences: () -> Unit,
    toSettings: () -> Unit,
    model: ForecastHomeModel = koinViewModel(),
) {
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
        data = state.forecast,
        period = state.period,
        loading = state.loading,
        refreshing = state.refreshing,
        permissionStatus = state.permissionStatus,
        snackbarHostState = snackbar.hostState,
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is ForecastHomeAction.Refresh -> model.fetch()
                is ForecastHomeAction.ChangePeriod -> model.updatePeriod(action.period)
                is ForecastHomeAction.ToViewDetails -> toViewDetails()
                is ForecastHomeAction.ToPreferences -> toPreferences()
                is ForecastHomeAction.ToSettings -> toSettings()
            }
        },
    )
}

@Composable
internal fun ForecastHomeScreen(
    location: Location?,
    preferences: Preferences,
    data: ForecastData?,
    dispatcher: Dispatcher<ForecastHomeAction>,
    modifier: Modifier = Modifier,
    period: ForecastPeriod = ForecastPeriod.Today,
    loading: Boolean = false,
    refreshing: Boolean = false,
    permissionStatus: LocationPermissionStatus = LocationPermissionStatus.Unknown,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val instant = data.rememberInstant()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                },
            )
        },
        bottomBar = {
            HomeBottomBar(
                toSettings = dispatcher.rememberRelay(ForecastHomeAction.ToSettings),
                toPreferences = dispatcher.rememberRelay(ForecastHomeAction.ToPreferences),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = AppTheme.spacing.standard)
                .fillMaxSize(),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            ) {
                Header(
                    data = data,
                    period = period,
                    changePeriod = dispatcher.rememberRelayOf(ForecastHomeAction::ChangePeriod),
                    instant = instant,
                )

                if (loading && data == null) {
                    // TODO: Initial loading status
                } else if (data != null) {
                    val periodData = remember(data, period) {
                        data.forPeriod(period)
                    }

                    if (periodData == null) {
                        // TODO: No data state
                    } else {
                        ForecastScoreContent(
                            updatedAt = instant,
                            preferences = preferences,
                            periodData = periodData,
                            onViewDetails = {},
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenPreview(data: ForecastData) {
    AppPreview {
        ForecastHomeScreen(
            location = null,
            data = data,
            preferences = Preferences.default,
            dispatcher = rememberDispatcher { },
        )
    }
}

@Preview
@Composable
private fun SunnyPreview() {
    ScreenPreview(PreviewData.Forecast.forecastData(PreviewData.Forecast.createSunnyForecast()))
}

@Preview
@Composable
private fun RainyPreview() {
    ScreenPreview(PreviewData.Forecast.forecastData(PreviewData.Forecast.createRainyForecast()))
}

@Preview
@Composable
private fun ColdPreview() {
    ScreenPreview(PreviewData.Forecast.forecastData(PreviewData.Forecast.createColdForecast()))
}

@Preview
@Composable
private fun WindyPreview() {
    ScreenPreview(PreviewData.Forecast.forecastData(PreviewData.Forecast.createWindyForecast()))
}
