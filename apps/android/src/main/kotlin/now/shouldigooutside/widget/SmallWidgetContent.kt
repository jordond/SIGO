package now.shouldigooutside.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
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
internal fun SmallWidgetContent(
    data: WidgetData?,
    strings: WidgetStrings,
    isDark: Boolean,
) {
    if (data == null) {
        EmptyWidgetContent(strings = strings, isDark = isDark)
        return
    }

    val colors = widgetColors(isDark)
    val scoreColor = colors.scoreColor(data.scoreResult)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
        modifier = GlanceModifier
            .fillMaxSize()
            .background(scoreColor)
            .padding(WidgetDimens.ContentPadding),
    ) {
        Text(
            text = strings.title,
            style = TextStyle(
                color = BlackActivityProvider,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )

        Spacer(modifier = GlanceModifier.height(WidgetDimens.TightSpacing))

        Text(
            text = data.scoreLabel.uppercase(),
            style = TextStyle(
                color = BlackProvider,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )

        Spacer(modifier = GlanceModifier.height(WidgetDimens.TightSpacing))

        Text(
            text = data.formattedTemp,
            style = TextStyle(
                color = BlackProvider,
                fontSize = 18.sp,
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
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )

        if (data.isStale) {
            Spacer(modifier = GlanceModifier.height(WidgetDimens.TightSpacing))
            Text(
                text = data.updatedAgoLabel,
                style = TextStyle(
                    color = BlackStaleProvider,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
            )
        }
    }
}

@Composable
internal fun EmptyWidgetContent(
    strings: WidgetStrings,
    isDark: Boolean,
    fontSize: TextUnit = 14.sp,
) {
    val colors = widgetColors(isDark)

    Text(
        text = strings.empty,
        style = TextStyle(
            color = colors.text.toProvider(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        ),
    )
}
