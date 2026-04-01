package now.shouldigooutside.forecast.ui.activities.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.scoreForPeriod
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.recommendation
import now.shouldigooutside.core.ui.AppTheme
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
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.mappers.units.icon
import now.shouldigooutside.core.ui.mappers.units.rememberUnit
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.forecast.ui.components.mappers.precipitationStatus
import now.shouldigooutside.forecast.ui.components.mappers.rememberText
import now.shouldigooutside.forecast.ui.components.mappers.temperatureStatus
import now.shouldigooutside.forecast.ui.components.mappers.windStatus
import kotlin.math.roundToInt

@Composable
internal fun ActivityScoreCard(
    period: ForecastPeriod,
    data: ActivityForecastScore,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    block: ForecastBlock? = null,
    units: Units? = null,
) {
    val colors = data.activity.colors()
    val score = remember(data, period) { data.score.scoreForPeriod(period) }
    ElevatedCard(
        colors = colors.cardColors(),
        onClick = onClick,
        modifier = modifier.widthIn(max = 400.dp),
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
                    modifier = Modifier.weight(1f),
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

            if (score != null && block != null && units != null) {
                HorizontalDivider()
                ActivityScoreContent(
                    score = score,
                    block = block,
                    units = units,
                    maxTemperature = data.preferences.maxTemperature.toDouble(),
                )
            }
        }
    }
}

@Composable
private fun ActivityScoreContent(
    score: Score,
    block: ForecastBlock,
    units: Units,
    maxTemperature: Double,
) {
    Column(
        modifier = Modifier.padding(AppTheme.spacing.standard),
    ) {
        Text(
            text = Res.string.recommendation
                .get()
                .uppercase(),
            style = AppTheme.typography.label3,
        )

        val recommendation = score.primaryRecommendation(
            temperatureValue = block.temperature.value,
            maxTemperature = maxTemperature,
        )
        Text(
            text = recommendation,
            style = AppTheme.typography.h2,
            modifier = Modifier.padding(bottom = AppTheme.spacing.small),
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val tempUnit = units.temperature.rememberUnit()
            WeatherValueCard(
                icon = units.temperature.icon(),
                value = "${block.temperature.value.roundToInt()}$tempUnit",
                colors = units.temperature.colors(),
            )

            val windUnit = units.windSpeed.rememberUnit()
            WeatherValueCard(
                icon = units.windSpeed.icon(),
                value = "${block.wind.speed.roundToInt()} $windUnit",
                colors = units.windSpeed.colors(),
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
                style = AppTheme.typography.h3,
            )
        }
    }
}

@Composable
private fun Score.primaryRecommendation(
    temperatureValue: Double,
    maxTemperature: Double,
): String {
    val r = reasons
    return when {
        r.temperature == ReasonValue.Outside -> r.temperatureStatus(temperatureValue, maxTemperature)
        r.wind == ReasonValue.Outside -> r.windStatus()
        r.precipitation == ReasonValue.Outside -> r.precipitationStatus()
        r.temperature == ReasonValue.Near -> r.temperatureStatus(temperatureValue, maxTemperature)
        r.wind == ReasonValue.Near -> r.windStatus()
        r.precipitation == ReasonValue.Near -> r.precipitationStatus()
        else -> r.temperatureStatus(temperatureValue, maxTemperature)
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
                units = forecast.units,
            )
        }
    }
}
