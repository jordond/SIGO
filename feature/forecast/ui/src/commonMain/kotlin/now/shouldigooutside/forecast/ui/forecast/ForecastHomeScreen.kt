package now.shouldigooutside.forecast.ui.forecast

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.stateholder.dispatcher.Dispatcher
import dev.stateholder.dispatcher.rememberDebounceDispatcher
import dev.stateholder.dispatcher.rememberDispatcher
import dev.stateholder.dispatcher.rememberRelay
import dev.stateholder.dispatcher.rememberRelayOf
import dev.stateholder.extensions.collectAsState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.forecast.WeatherBannerInfo
import now.shouldigooutside.core.model.forecast.blockForPeriod
import now.shouldigooutside.core.model.forecast.weatherBannerInfo
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.scoreForPeriod
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.LoadingBox
import now.shouldigooutside.core.ui.components.PullToRefreshBox
import now.shouldigooutside.core.ui.ktx.conditional
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.forecast.ui.components.Header
import now.shouldigooutside.forecast.ui.components.NoDataForPeriod
import now.shouldigooutside.forecast.ui.components.NoLocation
import now.shouldigooutside.forecast.ui.forecast.section.ForecastScoreContent
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Composable
internal fun ForecastHomeScreen(
    toViewDetails: () -> Unit,
    toLocationPicker: () -> Unit,
    model: ForecastHomeModel = koinViewModel(),
) {
    val state by model.collectAsState()
    ForecastHomeScreen(
        location = state.location,
        preferences = state.currentScore?.preferences ?: Preferences.default,
        units = state.units,
        data = state.forecast,
        currentBlock = state.currentBlock,
        currentPeriodScore = state.currentPeriodScore,
        bannerInfo = state.bannerInfo.takeIf { state.showBanner },
        period = state.period,
        loading = state.loading,
        refreshing = state.refreshing,
        selectedActivity = state.selectedActivity,
        activities = state.activities,
        dispatcher = rememberDebounceDispatcher { action ->
            when (action) {
                is ForecastHomeAction.Refresh -> model.fetch()
                is ForecastHomeAction.ChangePeriod -> model.update(action.period)
                is ForecastHomeAction.ToViewDetails -> toViewDetails()
                is ForecastHomeAction.OpenLocationSheet -> toLocationPicker()
                is ForecastHomeAction.ChangeActivity -> model.update(action.activity)
                is ForecastHomeAction.DismissBanner -> model.dismissBanner()
            }
        },
    )
}

@Composable
internal fun ForecastHomeScreen(
    location: Location?,
    preferences: Preferences,
    units: Units,
    data: Forecast?,
    dispatcher: Dispatcher<ForecastHomeAction>,
    modifier: Modifier = Modifier,
    currentBlock: ForecastBlock? = null,
    currentPeriodScore: Score? = null,
    bannerInfo: WeatherBannerInfo? = null,
    period: ForecastPeriod = ForecastPeriod.Today,
    loading: Boolean = false,
    refreshing: Boolean = false,
    selectedActivity: Activity = Activity.General,
    activities: PersistentList<Activity> = persistentListOf(),
) {
    PullToRefreshBox(
        modifier = modifier.statusBarsPadding(),
        isRefreshing = refreshing,
        onRefresh = dispatcher.rememberRelay(ForecastHomeAction.Refresh),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = AppTheme.spacing.standard)
                .fillMaxSize()
                .conditional(!loading) {
                    Modifier.verticalScroll(rememberScrollState())
                }.height(IntrinsicSize.Max),
        ) {
            val instant = remember(data) { data?.instant ?: Clock.System.now() }
            Header(
                period = period,
                changePeriod = dispatcher.rememberRelayOf(ForecastHomeAction::ChangePeriod),
                selectedActivity = selectedActivity,
                activities = activities,
                changeActivity = dispatcher.rememberRelayOf(ForecastHomeAction::ChangeActivity),
                location = location?.takeUnless { it.isDefaultName },
                onLocationClick = dispatcher.rememberRelay(ForecastHomeAction.OpenLocationSheet),
                instant = instant,
                modifier = Modifier.fillMaxWidth(),
            )

            val crossfadeTarget = remember(data, loading) { loading to data }
            Crossfade(
                targetState = crossfadeTarget,
                modifier = Modifier.padding(top = AppTheme.spacing.standard),
            ) { target ->
                if (target.first && target.second == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LoadingBox()
                    }
                } else if (target.second != null) {
                    if (currentBlock == null || currentPeriodScore == null) {
                        NoDataForPeriod()
                    } else {
                        ForecastScoreContent(
                            updatedAt = instant,
                            preferences = preferences,
                            units = units,
                            block = currentBlock,
                            score = currentPeriodScore,
                            bannerInfo = bannerInfo,
                            onScoreClick = dispatcher.rememberRelay(ForecastHomeAction.ToViewDetails),
                            onDismissBanner = dispatcher.rememberRelay(ForecastHomeAction.DismissBanner),
                            modifier = Modifier.padding(end = 2.dp),
                        )
                    }
                } else {
                    NoLocation(
                        onSetLocation = dispatcher.rememberRelay(ForecastHomeAction.OpenLocationSheet),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun NoLocationPreview() {
    AppPreview {
        ForecastHomeScreen(
            location = null,
            data = null,
            loading = false,
            preferences = Preferences.default,
            units = Units.Metric,
            dispatcher = rememberDispatcher { },
        )
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
            units = Units.Metric,
            dispatcher = rememberDispatcher { },
        )
    }
}

@Composable
private fun ScreenPreview(forecast: Forecast) {
    val forecastScore = remember { PreviewData.Forecast.score(forecast) }
    AppPreview {
        ForecastHomeScreen(
            location = null,
            data = forecast,
            currentBlock = forecast.blockForPeriod(ForecastPeriod.Today),
            currentPeriodScore = forecastScore.scoreForPeriod(ForecastPeriod.Today),
            preferences = Preferences.default,
            units = Units.Metric,
            dispatcher = rememberDispatcher { },
        )
    }
}

@Preview
@Composable
public fun SunnyPreview() {
    ScreenPreview(PreviewData.Forecast.createSunnyForecast())
}

@Preview
@Composable
private fun RainyPreview() {
    ScreenPreview(PreviewData.Forecast.createRainyForecast())
}

@Preview
@Composable
private fun ColdPreview() {
    ScreenPreview(PreviewData.Forecast.createColdForecast())
}

@Preview
@Composable
private fun WindyPreview() {
    ScreenPreview(PreviewData.Forecast.createWindyForecast())
}

@Preview
@Composable
private fun SevereWeatherLowPreview() {
    ScreenPreview(
        PreviewData.Forecast.createForecastFrom(
            PreviewData.Forecast.severeWeather(SevereWeatherRisk.Low),
        ),
    )
}

@Preview
@Composable
private fun SevereWeatherHighPreview() {
    ScreenPreview(
        PreviewData.Forecast.createForecastFrom(PreviewData.Forecast.severeWeather()),
    )
}

@Preview
@Composable
private fun GoodWeatherWindowPreview() {
    val forecast = PreviewData.Forecast.createGoodWindowForecast()
    val forecastScore = remember { PreviewData.Forecast.score(forecast) }
    val periodScore = remember(forecastScore) { forecastScore.scoreForPeriod(ForecastPeriod.Today) }
    AppPreview {
        ForecastHomeScreen(
            location = null,
            data = forecast,
            currentBlock = forecast.blockForPeriod(ForecastPeriod.Today),
            currentPeriodScore = periodScore,
            bannerInfo = remember(forecast, forecastScore, periodScore) {
                forecast.weatherBannerInfo(
                    score = forecastScore,
                    currentResult = periodScore?.result,
                    activity = Activity.General,
                    now = forecast.instant,
                )
            },
            preferences = Preferences.default,
            units = Units.Metric,
            dispatcher = rememberDispatcher { },
        )
    }
}
