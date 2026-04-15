package now.shouldigooutside.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import now.shouldigooutside.core.widget.WidgetData
import now.shouldigooutside.core.widget.WidgetStrings

@Composable
internal fun MediumWidgetContent(
    data: WidgetData?,
    strings: WidgetStrings,
    alertsText: String?,
    isDark: Boolean,
) {
    if (data == null) {
        EmptyWidgetContent(strings = strings, isDark = isDark)
        return
    }

    val colors = widgetColors(isDark)
    val scoreColor = colors.scoreColor(data.scoreResult)
    val textOnScore = colors.onSuccess

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        ScoreBadge(
            data = data,
            scoreColor = scoreColor,
            textOnScore = textOnScore,
        )

        Column(
            modifier = GlanceModifier
                .fillMaxHeight()
                .padding(
                    horizontal = WidgetDimens.MediumHorizontalPadding,
                    vertical = WidgetDimens.MediumVerticalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailRow(strings.feelsLike, data.formattedFeelsLike, colors.text, colors.textSecondary)
            Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
            DetailRow(strings.wind, data.formattedWind, colors.text, colors.textSecondary)
            Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
            DetailRow(strings.precip, "${data.precipChance}%", colors.text, colors.textSecondary)
            Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
            DetailRow(strings.today, data.todayScoreLabel, colors.text, colors.textSecondary)

            if (alertsText != null) {
                Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
                AlertBadge(alertsText = alertsText, color = colors.error)
            }

            if (data.isStale) {
                Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
                Text(
                    text = data.updatedAgoLabel,
                    style = TextStyle(
                        color = colors.textSecondary.toProvider(),
                        fontSize = 9.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun ScoreBadge(
    data: WidgetData,
    scoreColor: Color,
    textOnScore: Color,
) {
    Column(
        modifier = GlanceModifier
            .fillMaxHeight()
            .width(WidgetDimens.ScoreBadgeWidth)
            .background(scoreColor)
            .padding(WidgetDimens.ContentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val activityName = data.activityName
        if (activityName != null) {
            Text(
                text = activityName,
                style = TextStyle(
                    color = textOnScore.copy(alpha = WidgetDimens.ACTIVITY_ALPHA).toProvider(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )
        }

        Text(
            text = data.scoreLabel.uppercase(),
            style = TextStyle(
                color = textOnScore.toProvider(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = GlanceModifier.height(WidgetDimens.TightSpacing))

        Text(
            text = data.formattedTemp,
            style = TextStyle(
                color = textOnScore.toProvider(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = GlanceModifier.height(WidgetDimens.TightSpacing))

        Text(
            text = data.locationName,
            style = TextStyle(
                color = textOnScore.copy(alpha = WidgetDimens.LOCATION_ALPHA).toProvider(),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    textColor: Color,
    labelColor: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            style = TextStyle(
                color = labelColor.toProvider(),
                fontSize = 12.sp,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                color = textColor.toProvider(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
private fun AlertBadge(
    alertsText: String,
    color: Color,
) {
    Text(
        text = alertsText,
        style = TextStyle(
            color = color.toProvider(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        ),
    )
}
