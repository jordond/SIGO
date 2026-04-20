package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.forecast.WeatherBannerInfo
import now.shouldigooutside.core.model.forecast.WeatherReason
import now.shouldigooutside.core.model.forecast.WeatherWindow
import now.shouldigooutside.core.model.forecast.WindowQuality
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.ui.AppExperience
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.weather_banner_borderline_window_range
import now.shouldigooutside.core.resources.weather_banner_borderline_window_title
import now.shouldigooutside.core.resources.weather_banner_dismiss
import now.shouldigooutside.core.resources.weather_banner_go_now_title
import now.shouldigooutside.core.resources.weather_banner_go_now_until
import now.shouldigooutside.core.resources.weather_banner_go_now_until_reason
import now.shouldigooutside.core.resources.weather_banner_next_window_range
import now.shouldigooutside.core.resources.weather_banner_next_window_title
import now.shouldigooutside.core.resources.weather_banner_no_window_description
import now.shouldigooutside.core.resources.weather_banner_no_window_title
import now.shouldigooutside.core.resources.weather_reason_air_quality
import now.shouldigooutside.core.resources.weather_reason_precipitation
import now.shouldigooutside.core.resources.weather_reason_severe_weather
import now.shouldigooutside.core.resources.weather_reason_temperature
import now.shouldigooutside.core.resources.weather_reason_wind
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalAppExperience
import now.shouldigooutside.core.ui.activities.rememberDisplayName
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.IconButton
import now.shouldigooutside.core.ui.components.IconButtonDefaults
import now.shouldigooutside.core.ui.components.IconButtonVariant
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.CloudRain
import now.shouldigooutside.core.ui.icons.lucide.Hourglass
import now.shouldigooutside.core.ui.icons.lucide.Sun
import now.shouldigooutside.core.ui.icons.lucide.X
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.ktx.localTime
import now.shouldigooutside.core.ui.ktx.text
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import kotlin.time.Duration.Companion.hours

@Composable
internal fun WeatherWindowBanner(
    info: WeatherBannerInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    vertical = AppTheme.spacing.standard,
                    horizontal = AppTheme.spacing.small,
                ).padding(start = 4.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = AppTheme.colors.onPrimary.copy(alpha = 0.15f),
                        shape = AppTheme.shapes.small,
                    ),
            ) {
                Icon(
                    icon = info.icon(),
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.size(AppTheme.spacing.small))

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f),
            ) {
                when (info) {
                    is WeatherBannerInfo.GoNow -> {
                        GoNowContent(info)
                    }
                    is WeatherBannerInfo.NextWindow -> {
                        NextWindowContent(info)
                    }
                    WeatherBannerInfo.NoWindowToday -> {
                        WindowContent(
                            title = Res.string.weather_banner_no_window_title.get(),
                            body = Res.string.weather_banner_no_window_description.get(),
                        )
                    }
                }
            }

            IconButton(
                variant = IconButtonVariant.Ghost,
                shape = IconButtonDefaults.ButtonCircleShape,
                onClick = onDismiss,
            ) {
                Icon(
                    icon = AppIcons.Lucide.X,
                    contentDescription = Res.string.weather_banner_dismiss.get(),
                )
            }
        }
    }
}

@Composable
private fun GoNowContent(info: WeatherBannerInfo.GoNow) {
    val activityName = info.activity.rememberDisplayName()
    val endTime = info.endsAt.localTime().text()
    val reasonText = info.reason?.rememberDisplayName()

    WindowContent(
        title = Res.string.weather_banner_go_now_title.get(activityName),
        body = if (reasonText != null) {
            Res.string.weather_banner_go_now_until_reason.get(endTime, reasonText)
        } else {
            Res.string.weather_banner_go_now_until.get(endTime)
        },
    )
}

@Composable
private fun NextWindowContent(info: WeatherBannerInfo.NextWindow) {
    val startText = info.window.start
        .localTime()
        .text()
    val endText = info.window.end
        .localTime()
        .text()
    val (titleRes, rangeRes) = when (info.quality) {
        WindowQuality.Good -> {
            Res.string.weather_banner_next_window_title to Res.string.weather_banner_next_window_range
        }
        WindowQuality.Borderline -> {
            Res.string.weather_banner_borderline_window_title to
                Res.string.weather_banner_borderline_window_range
        }
    }

    WindowContent(
        title = titleRes.get(),
        body = rangeRes.get(startText, endText),
    )
}

@Composable
private fun WindowContent(
    title: String,
    body: String,
) {
    Text(
        text = title,
        style = AppTheme.typography.h3,
        autoSize = AppTheme.typography.h3.autoSize(),
        maxLines = 1,
    )
    Text(
        text = body,
        style = AppTheme.typography.body1,
    )
}

private fun WeatherBannerInfo.icon(): ImageVector =
    when (this) {
        is WeatherBannerInfo.GoNow -> AppIcons.Lucide.Sun
        is WeatherBannerInfo.NextWindow -> AppIcons.Lucide.Hourglass
        WeatherBannerInfo.NoWindowToday -> AppIcons.Lucide.CloudRain
    }

@Composable
private fun WeatherReason.rememberDisplayName(): String =
    when (this) {
        WeatherReason.Wind -> Res.string.weather_reason_wind
        WeatherReason.Temperature -> Res.string.weather_reason_temperature
        WeatherReason.Precipitation -> Res.string.weather_reason_precipitation
        WeatherReason.SevereWeather -> Res.string.weather_reason_severe_weather
        WeatherReason.AirQuality -> Res.string.weather_reason_air_quality
    }.get()

@Preview
@Composable
private fun WeatherWindowBannerPreview() {
    val forecast = PreviewData.Forecast.createGoodWindowForecast()
    val goNow = WeatherBannerInfo.GoNow(
        endsAt = forecast.instant + 3.hours,
        reason = WeatherReason.Precipitation,
        activity = Activity.Walking,
    )
    val nextWindow = WeatherBannerInfo.NextWindow(
        window = previewWindow(forecast),
        quality = WindowQuality.Good,
    )
    val borderlineWindow = WeatherBannerInfo.NextWindow(
        window = previewWindow(forecast),
        quality = WindowQuality.Borderline,
    )

    AppPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            modifier = Modifier.padding(AppTheme.spacing.standard),
        ) {
            WeatherWindowBanner(info = goNow, onDismiss = {}, modifier = Modifier.fillMaxWidth())
            WeatherWindowBanner(info = nextWindow, onDismiss = {}, modifier = Modifier.fillMaxWidth())
            WeatherWindowBanner(info = borderlineWindow, onDismiss = {}, modifier = Modifier.fillMaxWidth())
            WeatherWindowBanner(
                info = WeatherBannerInfo.NoWindowToday,
                onDismiss = {},
                modifier = Modifier.fillMaxWidth(),
            )

            CompositionLocalProvider(
                LocalAppExperience provides AppExperience.default.copy(use24HourFormat = true),
            ) {
                WeatherWindowBanner(info = goNow, onDismiss = {}, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

private fun previewWindow(forecast: Forecast): WeatherWindow =
    PreviewData.Forecast.goodWindow(forecast)
        ?: WeatherWindow(
            start = forecast.instant + 2.hours,
            end = forecast.instant + 5.hours,
        )
