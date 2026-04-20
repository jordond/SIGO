package now.shouldigooutside.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontStyle
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

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(colors.surface),
    ) {
        ScoreBadgeContent(
            data = data,
            color = scoreColor,
            modifier = GlanceModifier
                .fillMaxSize()
                .defaultWeight(),
        )

        Column(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .defaultWeight(),
        ) {
            val activityName = data.activityName
            if (activityName != null) {
                BrutalDetailRow(strings.activity, activityName)
                Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
            }
            BrutalDetailRow(strings.feelsLike, data.formattedFeelsLike)
            Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
            BrutalDetailRow(strings.wind, data.formattedWind)
            Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
            BrutalDetailRow(strings.precip, "${data.precipChance}%")
            Spacer(modifier = GlanceModifier.height(WidgetDimens.RowSpacing))
            BrutalDetailRow(strings.today, data.todayScoreLabel)

            if (data.isStale) {
                Spacer(modifier = GlanceModifier.height(WidgetDimens.TightSpacing))
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
private fun ScoreBadgeContent(
    data: WidgetData,
    color: Color,
    modifier: GlanceModifier = GlanceModifier,
) {
    Column(
        modifier = modifier
            .background(color)
            .padding(
                horizontal = WidgetDimens.ScoreBadgeHorizontalPadding,
                vertical = WidgetDimens.ScoreBadgeVerticalPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = data.scoreLabel.uppercase(),
            style = TextStyle(
                color = BlackProvider,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )

        Text(
            text = data.formattedTemp,
            style = TextStyle(
                color = BlackProvider,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )

        Spacer(modifier = GlanceModifier.height(WidgetDimens.TightSpacing))

        Text(
            text = data.locationName,
            style = TextStyle(
                color = BlackLocationProvider,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )
    }
}

@Composable
private fun BrutalDetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(),
            style = TextStyle(
                color = BlackProvider,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = GlanceModifier.defaultWeight(),
        )
        Text(
            text = value,
            style = TextStyle(
                color = BlackProvider,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
        )
    }
}
