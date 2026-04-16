package now.shouldigooutside.forecast.ui.activities.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.Metric
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.score.scoreForPeriod
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.AqiLevels
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.activities.colors
import now.shouldigooutside.core.ui.activities.rememberDisplayName
import now.shouldigooutside.core.ui.activities.rememberIcon
import now.shouldigooutside.core.ui.asDisplay
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.CloudRain
import now.shouldigooutside.core.ui.icons.lucide.Thermometer
import now.shouldigooutside.core.ui.icons.lucide.TriangleAlert
import now.shouldigooutside.core.ui.icons.lucide.Waves
import now.shouldigooutside.core.ui.icons.lucide.Wind
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preferences.rememberEnabledMetrics
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.forecast.ui.components.mappers.color
import now.shouldigooutside.forecast.ui.components.mappers.precipitationStatus
import now.shouldigooutside.forecast.ui.components.mappers.rememberText
import now.shouldigooutside.forecast.ui.components.mappers.severeWeatherStatus
import now.shouldigooutside.forecast.ui.components.mappers.temperatureStatus
import now.shouldigooutside.forecast.ui.components.mappers.windStatus

@Composable
internal fun ActivityScoreCard(
    period: ForecastPeriod,
    data: ActivityForecastScore,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    block: ForecastBlock? = null,
) {
    val colors = data.activity.colors()
    val score = remember(data, period) { data.score.scoreForPeriod(period) }
    ElevatedCard(
        colors = colors.cardColors(),
        onClick = onClick,
        modifier = modifier,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(colors.high)
                    .padding(vertical = AppTheme.spacing.small, horizontal = AppTheme.spacing.standard),
            ) {
                Icon(
                    icon = data.activity.rememberIcon(),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )

                Text(
                    text = data.activity.rememberDisplayName(),
                    style = AppTheme.typography.h2,
                    autoSize = AppTheme.typography.h2.autoSize(),
                    modifier = Modifier
                        .padding(horizontal = AppTheme.spacing.mini)
                        .weight(1f),
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(75.dp)
                        .background(Color.Black),
                ) {
                    val text = score?.result?.rememberText() ?: ""
                    Text(
                        text = text.uppercase(),
                        maxLines = 1,
                        color = Color.White,
                        autoSize = AppTheme.typography.body1.autoSize(),
                        style = AppTheme.typography.body1.asDisplay
                            .copy(letterSpacing = -(2).sp),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(AppTheme.spacing.small),
                    )
                }
            }

            if (score != null && block != null && score.result != ScoreResult.Yes) {
                val enabled = rememberEnabledMetrics(data.preferences)
                HorizontalDivider()
                LimitingFactors(
                    reasons = score.reasons,
                    enabled = enabled,
                    temperatureValue = block.temperature.value,
                    maxTemperature = data.preferences.maxTemperature.toDouble(),
                    airQuality = block.airQuality,
                    modifier = Modifier.padding(
                        horizontal = AppTheme.spacing.standard,
                        vertical = AppTheme.spacing.small,
                    ),
                )
            }
        }
    }
}

@Composable
private fun LimitingFactors(
    reasons: Reasons,
    enabled: Set<Metric>,
    temperatureValue: Double,
    maxTemperature: Double,
    airQuality: AirQuality,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        if (Metric.SevereWeather in enabled && reasons.severeWeather != ReasonValue.Inside) {
            WeatherValueCard(
                icon = AppIcons.Lucide.TriangleAlert,
                value = reasons.severeWeatherStatus(),
                colors = reasons.severeWeather.toResult().color(),
            )
        }

        if (Metric.Temperature in enabled && reasons.temperature != ReasonValue.Inside) {
            WeatherValueCard(
                icon = AppIcons.Lucide.Thermometer,
                value = reasons.temperatureStatus(temperatureValue, maxTemperature),
                colors = reasons.temperature.toResult().color(),
            )
        }

        if (Metric.Wind in enabled && reasons.wind != ReasonValue.Inside) {
            WeatherValueCard(
                icon = AppIcons.Lucide.Wind,
                value = reasons.windStatus(),
                colors = reasons.wind.toResult().color(),
            )
        }

        if (Metric.Precipitation in enabled && reasons.precipitation != ReasonValue.Inside) {
            WeatherValueCard(
                icon = AppIcons.Lucide.CloudRain,
                value = reasons.precipitationStatus(),
                colors = reasons.precipitation.toResult().color(),
            )
        }

        if (Metric.AirQuality in enabled && reasons.airQuality != ReasonValue.Inside) {
            val aqiLevel = AqiLevels.forValue(airQuality)
            WeatherValueCard(
                icon = AppIcons.Lucide.Waves,
                value = aqiLevel.title.get(),
                colors = aqiLevel.colors,
            )
        }
    }
}

@Composable
private fun WeatherValueCard(
    icon: ImageVector,
    value: String,
    colors: BrutalColors,
) {
    Card(
        colors = colors.cardColors(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(
                horizontal = AppTheme.spacing.standard,
                vertical = AppTheme.spacing.small,
            ),
        ) {
            Icon(
                icon = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = value,
                style = AppTheme.typography.h4,
            )
        }
    }
}

private class Params : PreviewParameterProvider<ActivityForecastScore> {
    override val values: Sequence<ActivityForecastScore> = sequenceOf(
        PreviewData.activityScore(Activity.Running, PreviewData.Score.yes),
        PreviewData.activityScore(Activity.Running, PreviewData.Score.maybe),
        PreviewData.activityScore(Activity.Running, PreviewData.Score.no),
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview(
    @PreviewParameter(Params::class) score: ActivityForecastScore,
) {
    val forecast = PreviewData.Forecast.createForecast()
    AppPreview {
        Box(Modifier.padding(12.dp)) {
            ActivityScoreCard(
                period = ForecastPeriod.Now,
                data = score,
                onClick = {},
                block = forecast.current,
            )
        }
    }
}
