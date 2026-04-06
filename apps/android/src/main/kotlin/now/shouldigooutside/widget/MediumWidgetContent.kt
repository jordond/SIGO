package now.shouldigooutside.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
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
    updatedAgoText: String?,
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
        // Left side: score badge
        Column(
            modifier = GlanceModifier
                .fillMaxHeight()
                .width(120.dp)
                .background(scoreColor)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (data.showActivityName) {
                Text(
                    text = data.activityName.orEmpty(),
                    style = TextStyle(
                        color = textOnScore.copy(alpha = 0.7f).toProvider(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    ),
                )
            }

            Text(
                text = data.scoreResult.name.uppercase(),
                style = TextStyle(
                    color = textOnScore.toProvider(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = data.formattedTemp,
                style = TextStyle(
                    color = textOnScore.toProvider(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = data.locationName,
                style = TextStyle(
                    color = textOnScore.copy(alpha = 0.8f).toProvider(),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
            )
        }

        // Right side: weather details
        Column(
            modifier = GlanceModifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DetailRow(
                label = strings.feelsLike,
                value = data.formattedFeelsLike,
                textColor = colors.text,
                labelColor = colors.textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = strings.wind,
                value = data.formattedWind,
                textColor = colors.text,
                labelColor = colors.textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = strings.precip,
                value = "${data.precipChance}%",
                textColor = colors.text,
                labelColor = colors.textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = strings.today,
                value = data.todayScoreResult.name,
                textColor = colors.text,
                labelColor = colors.textSecondary,
            )

            if (alertsText != null) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = alertsText,
                    style = TextStyle(
                        color = colors.error.toProvider(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            if (data.isStale && updatedAgoText != null) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = updatedAgoText,
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
private fun DetailRow(
    label: String,
    value: String,
    textColor: androidx.compose.ui.graphics.Color,
    labelColor: androidx.compose.ui.graphics.Color,
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
