package app.sigot.forecast.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.forecast.SevereWeatherRisk
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.snackbar.Snackbar
import app.sigot.core.ui.components.snackbar.SnackbarHost
import app.sigot.core.ui.components.snackbar.SnackbarHostState
import app.sigot.core.ui.components.snackbar.rememberSnackbarProvider
import app.sigot.core.ui.ktx.conditional
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.PreviewData
import app.sigot.forecast.ui.ForecastHomeModel.Event
import app.sigot.forecast.ui.components.Header
import app.sigot.forecast.ui.components.LoadingBox
import app.sigot.forecast.ui.components.NoDataForPeriod
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

    LaunchedEffect(Unit) {
        // Try to fetch on the first load, this could hit the cache, or a fresh forecast.
        model.fetch()
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
                is ForecastHomeAction.ToViewDetails -> model.fetch()
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
                canGoToDetails = !loading && data != null,
                toDetails = dispatcher.rememberRelay(ForecastHomeAction.ToViewDetails),
                toSettings = dispatcher.rememberRelay(ForecastHomeAction.ToSettings),
                toPreferences = dispatcher.rememberRelay(ForecastHomeAction.ToPreferences),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = dispatcher.rememberRelay(ForecastHomeAction.Refresh),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = AppTheme.spacing.standard)
                    .fillMaxSize()
                    .conditional(!loading) {
                        Modifier.verticalScroll(rememberScrollState())
                    }.height(IntrinsicSize.Max)
                    .padding(innerPadding),
            ) {
                Header(
                    data = data,
                    period = period,
                    changePeriod = dispatcher.rememberRelayOf(ForecastHomeAction::ChangePeriod),
                    instant = instant,
                    modifier = Modifier.padding(top = AppTheme.spacing.standard),
                )

                val crossfadeTarget = remember(data, loading) { loading to data }
                Crossfade(
                    targetState = crossfadeTarget,
                    modifier = Modifier.padding(top = AppTheme.spacing.standard),
                ) { target ->
                    if (target.first && target.second == null) {
                        Box(
                            // contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            LoadingBox()
                        }
                    } else if (target.second != null) {
                        val periodData = remember(target.second, period) {
                            target.second?.forPeriod(period)
                        }

                        if (periodData == null) {
                            NoDataForPeriod()
                        } else {
                            ForecastScoreContent(
                                updatedAt = instant,
                                preferences = preferences,
                                periodData = periodData,
                                modifier = Modifier.padding(end = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoadingPreview() {
    AppPreview {
        ForecastHomeScreen(
            location = null,
            data = null,
            loading = true,
            preferences = Preferences.default,
            dispatcher = rememberDispatcher { },
        )
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

@Preview
@Composable
private fun SevereWeatherLowPreview() {
    val forecast = PreviewData.Forecast.createForecastFrom(
        PreviewData.Forecast.severeWeather(
            SevereWeatherRisk.Low,
        ),
    )
    ScreenPreview(PreviewData.Forecast.forecastData(forecast))
}

@Preview
@Composable
private fun SevereWeatherHighPreview() {
    val forecast = PreviewData.Forecast.createForecastFrom(PreviewData.Forecast.severeWeather())
    ScreenPreview(PreviewData.Forecast.forecastData(forecast))
}
