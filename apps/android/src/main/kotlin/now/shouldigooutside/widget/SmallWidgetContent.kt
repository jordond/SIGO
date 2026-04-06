package now.shouldigooutside.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
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
import now.shouldigooutside.core.widget.WidgetData
import now.shouldigooutside.core.widget.WidgetStrings

@Composable
internal fun SmallWidgetContent(
    data: WidgetData?,
    strings: WidgetStrings,
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
                color = textOnScore.toProvider(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = GlanceModifier.height(4.dp))

        Text(
            text = data.formattedTemp,
            style = TextStyle(
                color = textOnScore.toProvider(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
        )

        Spacer(modifier = GlanceModifier.height(2.dp))

        Text(
            text = data.locationName,
            style = TextStyle(
                color = textOnScore.copy(alpha = 0.8f).toProvider(),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )

        if (data.isStale && updatedAgoText != null) {
            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = updatedAgoText,
                style = TextStyle(
                    color = textOnScore.copy(alpha = 0.6f).toProvider(),
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                ),
            )
        }
    }
}

@Composable
internal fun EmptyWidgetContent(
    strings: WidgetStrings,
    isDark: Boolean,
) {
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
            text = strings.empty,
            style = TextStyle(
                color = colors.text.toProvider(),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
            ),
        )
    }
}
