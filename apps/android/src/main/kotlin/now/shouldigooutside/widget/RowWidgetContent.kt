package now.shouldigooutside.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import now.shouldigooutside.core.widget.WidgetData
import now.shouldigooutside.core.widget.WidgetStrings

@Composable
internal fun RowWidgetContent(
    data: WidgetData?,
    strings: WidgetStrings,
    isDark: Boolean,
    wide: Boolean,
) {
    if (data == null) {
        EmptyWidgetContent(strings = strings, isDark = isDark, fontSize = 12.sp)
        return
    }

    val colors = widgetColors(isDark)
    val scoreColor = colors.scoreColor(data.scoreResult)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier
            .fillMaxSize()
            .background(scoreColor)
            .padding(horizontal = WidgetDimens.RowHorizontalPadding),
    ) {
        Column(
            modifier = GlanceModifier.defaultWeight(),
        ) {
            Text(
                text = strings.title,
                style = TextStyle(
                    color = BlackActivityProvider,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                ),
            )

            ScoreText(
                label = data.scoreLabel.uppercase(),
                size = if (wide) 50.sp else 36.sp,
            )
        }

        Column {
            Text(
                text = data.formattedTemp,
                style = TextStyle(
                    color = BlackProvider,
                    fontSize = if (wide) 16.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
            )
            Text(
                text = data.locationName,
                style = TextStyle(
                    color = BlackLocationProvider,
                    fontSize = 11.sp,
                ),
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ScoreText(
    label: String,
    size: TextUnit,
    modifier: GlanceModifier = GlanceModifier,
) {
    Text(
        text = label,
        style = TextStyle(
            color = BlackProvider,
            fontSize = size,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center,
        ),
        maxLines = 1,
        modifier = modifier,
    )
}
