package now.shouldigooutside.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import now.shouldigooutside.core.widget.WidgetData
import kotlin.math.roundToInt

@Composable
internal fun SmallWidgetContent(
    data: WidgetData?,
    isDark: Boolean,
) {
    if (data == null) {
        EmptyWidgetContent(isDark)
        return
    }

    val colors = widgetColors(isDark)
    val scoreColor = colors.scoreColor(data.scoreResult)
    val textOnScore = colors.onSuccess

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(scoreColor)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = data.scoreResult.name.uppercase(),
            style = TextStyle(
                color = ColorProvider(textOnScore),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        Text(
            text = "${data.currentTemp.roundToInt()}°${data.tempUnit.name.first()}",
            style = TextStyle(
                color = ColorProvider(textOnScore),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = GlanceModifier.height(2.dp))

        Text(
            text = data.locationName,
            style = TextStyle(
                color = ColorProvider(textOnScore.copy(alpha = 0.8f)),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )

        if (data.isStale) {
            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = data.updatedAgo(LocalContext.current),
                style = TextStyle(
                    color = ColorProvider(textOnScore.copy(alpha = 0.6f)),
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
internal fun EmptyWidgetContent(isDark: Boolean) {
    val context = LocalContext.current
    val colors = widgetColors(isDark)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(colors.surface)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = context.getString(R.string.widget_empty),
            style = TextStyle(
                color = ColorProvider(colors.text),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
