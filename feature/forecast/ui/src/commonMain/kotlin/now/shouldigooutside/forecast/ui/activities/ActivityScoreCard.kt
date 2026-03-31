package now.shouldigooutside.forecast.ui.activities

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.scoreForPeriod
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.activities.colors
import now.shouldigooutside.core.ui.activities.rememberDisplayName
import now.shouldigooutside.core.ui.activities.rememberIcon
import now.shouldigooutside.core.ui.asDisplay
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.forecast.ui.components.mappers.rememberText

@Composable
internal fun ActivityScoreCard(
    period: ForecastPeriod,
    data: ActivityForecastScore,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
                    .padding(AppTheme.spacing.standard),
            ) {
                Icon(
                    icon = data.activity.rememberIcon(),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                )

                Text(
                    text = data.activity.rememberDisplayName(),
                    style = AppTheme.typography.h2,
                    autoSize = AppTheme.typography.h2.autoSize(),
                    modifier = Modifier
                        .padding(horizontal = AppTheme.spacing.standard)
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
    AppPreview {
        Box(Modifier.padding(12.dp)) {
            ActivityScoreCard(
                period = ForecastPeriod.Now,
                data = score,
                onClick = {},
            )
        }
    }
}
