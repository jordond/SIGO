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
import androidx.glance.unit.ColorProvider
import now.shouldigooutside.core.widget.WidgetData
import kotlin.math.roundToInt

@Composable
internal fun MediumWidgetContent(
    data: WidgetData?,
    isDark: Boolean,
) {
    if (data == null) {
        EmptyWidgetContent(isDark)
        return
    }

    val scoreColor = WidgetTheme.scoreColor(data.scoreResult, isDark)
    val textOnScore = WidgetTheme.scoreTextColor(data.scoreResult)
    val surfaceColor = WidgetTheme.surfaceColor(isDark)
    val textColor = WidgetTheme.textColor(isDark)
    val textSecondary = WidgetTheme.textSecondaryColor(isDark)

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(surfaceColor),
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
            if (data.activityName != WidgetData.DEFAULT_ACTIVITY_NAME) {
                Text(
                    text = data.activityName,
                    style = TextStyle(
                        color = ColorProvider(textOnScore.copy(alpha = 0.7f)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    ),
                )
            }

            Text(
                text = data.scoreResult.uppercase(),
                style = TextStyle(
                    color = ColorProvider(textOnScore),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = "${data.currentTemp.roundToInt()}°${data.tempUnit.first()}",
                style = TextStyle(
                    color = ColorProvider(textOnScore),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = data.locationName,
                style = TextStyle(
                    color = ColorProvider(textOnScore.copy(alpha = 0.8f)),
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
                label = "Feels like",
                value = "${data.feelsLikeTemp.roundToInt()}°${data.tempUnit.first()}",
                textColor = textColor,
                labelColor = textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = "Wind",
                value = "${data.windSpeed.roundToInt()} ${formatWindUnit(data.windSpeedUnit)}",
                textColor = textColor,
                labelColor = textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = "Precip",
                value = "${data.precipChance}%",
                textColor = textColor,
                labelColor = textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = "Today",
                value = data.todayScoreResult,
                textColor = textColor,
                labelColor = textSecondary,
            )

            if (data.alertCount > 0) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "${data.alertCount} alert${if (data.alertCount > 1) "s" else ""}",
                    style = TextStyle(
                        color = ColorProvider(WidgetTheme.scoreNoLight),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
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
                color = ColorProvider(labelColor),
                fontSize = 12.sp,
            ),
        )
        Text(
            text = value,
            style = TextStyle(
                color = ColorProvider(textColor),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

private fun formatWindUnit(unit: String): String =
    when (unit) {
        "MilePerHour" -> "mph"
        "KilometerPerHour" -> "km/h"
        "MeterPerSecond" -> "m/s"
        else -> unit
    }
