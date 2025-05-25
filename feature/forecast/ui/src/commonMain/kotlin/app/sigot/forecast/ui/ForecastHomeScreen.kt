package app.sigot.forecast.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.DropdownMenu
import app.sigot.core.ui.components.DropdownMenuItem
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.snackbar.Snackbar
import app.sigot.core.ui.components.snackbar.SnackbarHost
import app.sigot.core.ui.components.snackbar.SnackbarHostState
import app.sigot.core.ui.components.snackbar.rememberSnackbarProvider
import app.sigot.core.ui.mappers.rememberText
import app.sigot.core.ui.preview.AppPreview
import app.sigot.forecast.ui.ForecastHomeModel.Event
import app.sigot.forecast.ui.components.HeaderText
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberDispatcher
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
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is ForecastHomeAction.Refresh -> {}
                is ForecastHomeAction.ChangePeriod -> model.updatePeriod(action.period)
            }
        },
    )
}

internal sealed interface ForecastHomeAction {
    data object Refresh : ForecastHomeAction

    data class ChangePeriod(
        val period: ForecastPeriod,
    ) : ForecastHomeAction
}

@Composable
internal fun ForecastHomeScreen(
    location: Location?,
    preferences: Preferences,
    forecast: ForecastData?,
    dispatcher: Dispatcher<ForecastHomeAction>,
    modifier: Modifier = Modifier,
    period: ForecastPeriod = ForecastPeriod.Today,
    loading: Boolean = false,
    refreshing: Boolean = false,
    permissionStatus: LocationPermissionStatus = LocationPermissionStatus.Unknown,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val instant = remember(forecast) {
        forecast?.forecast?.instant ?: Clock.System.now()
    }
    var showPeriodDropdown by remember { mutableStateOf(false) }
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
            Column(
                modifier = Modifier.wrapContentSize(Alignment.TopEnd),
            ) {
                HeaderText(
                    instant = forecast?.forecast?.instant ?: Clock.System.now(),
                    period = period,
                    onClick = { showPeriodDropdown = !showPeriodDropdown },
                )

                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopCenter)
                        .align(Alignment.CenterHorizontally),
                ) {
                    DropdownMenu(
                        expanded = showPeriodDropdown,
                        onDismissRequest = { showPeriodDropdown = false },
                    ) {
                        val entries = remember(period) {
                            ForecastPeriod.entries - period
                        }
                        entries.forEachIndexed { index, entry ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = entry.rememberText(instant),
                                        style = AppTheme.typography.h3,
                                    )
                                },
                                onClick = {
                                    showPeriodDropdown = false
                                    dispatcher.dispatch(ForecastHomeAction.ChangePeriod(entry))
                                },
                            )

                            if (index != entries.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
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
            dispatcher = rememberDispatcher { },
        )
    }
}
