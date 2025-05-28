package app.sigot.forecast.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.BaseUnit
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.asContent
import app.sigot.core.ui.cardColors
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Scaffold
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.components.snackbar.Snackbar
import app.sigot.core.ui.components.snackbar.SnackbarHost
import app.sigot.core.ui.components.snackbar.SnackbarHostState
import app.sigot.core.ui.components.snackbar.rememberSnackbarProvider
import app.sigot.core.ui.mappers.units.colors
import app.sigot.core.ui.mappers.units.rememberIcon
import app.sigot.core.ui.mappers.units.rememberTitle
import app.sigot.core.ui.mappers.units.rememberUnit
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.PreviewData
import app.sigot.forecast.ui.ForecastHomeModel.Event
import app.sigot.forecast.ui.components.Header
import app.sigot.forecast.ui.components.mappers.colors
import app.sigot.forecast.ui.components.mappers.rememberInstant
import app.sigot.forecast.ui.components.mappers.rememberScoreText
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
                is ForecastHomeAction.Refresh -> model.forceRefresh()
                is ForecastHomeAction.ChangePeriod -> model.updatePeriod(action.period)
                is ForecastHomeAction.ToPreferences -> toPreferences()
                is ForecastHomeAction.ToSettings -> toSettings()
            }
        },
    )
}

internal sealed interface ForecastHomeAction {
    data object Refresh : ForecastHomeAction

    data object ToSettings : ForecastHomeAction

    data object ToPreferences : ForecastHomeAction

    data class ChangePeriod(
        val period: ForecastPeriod,
    ) : ForecastHomeAction
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
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            Header(
                data = data,
                period = period,
                changePeriod = dispatcher.rememberRelayOf(ForecastHomeAction::ChangePeriod),
                instant = instant,
                modifier = Modifier.weight(1f),
            )

            if (loading && data == null) {
                // TODO: Initial loading status
            } else if (data != null) {
                val scoreData = remember(data, period) {
                    data.forPeriod(period)
                }

                if (scoreData == null) {
                    // TODO: No data state
                } else {
                    Column(
                        modifier = Modifier.weight(3f),
                    ) {
                        val (containerColor, contentColor) = scoreData.colors()
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = containerColor,
                                contentColor = contentColor,
                            ),
                            modifier = Modifier
                                .weight(.8f)
                                .heightIn(min = 200.dp),
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxSize(),
                            ) {
                                val text = scoreData.rememberScoreText()
                                Text(
                                    text = text,
                                    maxLines = 1,
                                    autoSize = TextAutoSize.StepBased(maxFontSize = 200.sp),
                                    style = AppTheme.typography.h2.copy(letterSpacing = (-20).sp),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .weight(1.5f)
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                        ) {
                            PreferenceCard(
                                unit = preferences.units.temperature,
                                data = scoreData.forecast.temperature,
                                text = { unit ->
                                    "${scoreData.forecast.temperature.value.toInt()} $unit"
                                },
                                modifier = Modifier.weight(1f),
                            )

                            PreferenceCard(
                                unit = preferences.units.windSpeed,
                                data = scoreData.forecast.wind,
                                text = { unit ->
                                    "${scoreData.forecast.wind.speed.toInt()} $unit"
                                },
                                modifier = Modifier.weight(1f),
                            )

                            PreferenceCard(
                                unit = preferences.units.precipitation,
                                data = scoreData.forecast.precipitation,
                                text = { unit ->
                                    "${scoreData.forecast.precipitation.amount.toInt()} $unit"
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 16.dp),
                        ) {
                            Button(
                                variant = ButtonVariant.PrimaryElevated,
                                text = "View Details",
                                textStyle = AppTheme.typography.h2,
                                onClick = {},
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun <T : BaseUnit, D> PreferenceCard(
    unit: T,
    data: D,
    // TODO: Temporary
    text: (String) -> String,
    modifier: Modifier = Modifier,
) {
    val colors = unit.colors()
    Card(
        colors = colors.cardColors(),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 150.dp).fillMaxWidth(),
        ) {
            Text(
                text = unit.rememberTitle().uppercase(),
                maxLines = 1,
                style = AppTheme.typography.h4,
                textAlign = TextAlign.Center,
                autoSize = TextAutoSize.StepBased(maxFontSize = AppTheme.typography.h4.fontSize),
                modifier = Modifier
                    .background(colors.bright)
                    .padding(8.dp)
                    .fillMaxWidth(),
            )

            HorizontalDivider()

            Column(
                modifier = Modifier.padding(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        icon = unit.rememberIcon(),
                        modifier = Modifier
                            .weight(2f)
                            .heightIn(max = 84.dp)
                            .fillMaxSize(),
                    )

                    Text(
                        text = text(unit.rememberUnit()),
                        maxLines = 1,
                        autoSize = TextAutoSize.StepBased(maxFontSize = AppTheme.typography.h1.fontSize),
                        style = AppTheme.typography.h3.asContent,
                        modifier = Modifier.weight(3f),
                    )
                }

                Text("The temperature is acceptable")
            }
        }
    }
}

@Preview
@Composable
private fun ForecastHomeScreenPreview() {
    val data = remember {
        PreviewData.Forecast.forecastData(
            forecast = PreviewData.Forecast.createSunnyForecast(),
        )
    }
    AppPreview {
        ForecastHomeScreen(
            location = null,
            data = data,
            preferences = Preferences.default,
            dispatcher = rememberDispatcher { },
        )
    }
}
