package now.shouldigooutside.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
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
import now.shouldigooutside.core.model.units.WindSpeedUnit
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

    val context = LocalContext.current
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
                text = data.scoreResult.name.uppercase(),
                style = TextStyle(
                    color = ColorProvider(textOnScore),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = "${data.currentTemp.roundToInt()}°${data.tempUnit.name.first()}",
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
                label = context.getString(R.string.widget_feels_like),
                value = "${data.feelsLikeTemp.roundToInt()}°${data.tempUnit.name.first()}",
                textColor = colors.text,
                labelColor = colors.textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = context.getString(R.string.widget_wind),
                value = "${data.windSpeed.roundToInt()} ${formatWindUnit(data.windSpeedUnit)}",
                textColor = colors.text,
                labelColor = colors.textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = context.getString(R.string.widget_precip),
                value = "${data.precipChance}%",
                textColor = colors.text,
                labelColor = colors.textSecondary,
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            DetailRow(
                label = context.getString(R.string.widget_today),
                value = data.todayScoreResult.name,
                textColor = colors.text,
                labelColor = colors.textSecondary,
            )

            if (data.alertCount > 0) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = context.resources.getQuantityString(
                        R.plurals.widget_alerts,
                        data.alertCount,
                        data.alertCount,
                    ),
                    style = TextStyle(
                        color = ColorProvider(colors.error),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            if (data.isStale) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = data.updatedAgo(context),
                    style = TextStyle(
                        color = ColorProvider(colors.textSecondary),
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

private fun formatWindUnit(unit: WindSpeedUnit): String =
    when (unit) {
        WindSpeedUnit.MilePerHour -> "mph"
        WindSpeedUnit.KilometerPerHour -> "km/h"
        WindSpeedUnit.MeterPerSecond -> "m/s"
        WindSpeedUnit.Knot -> "kn"
    }
