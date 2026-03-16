package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Surface
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.rememberText
import now.shouldigooutside.core.ui.preview.AppPreview
import kotlin.time.Instant

private const val INLINE_CONTENT_ID = "inlineContentId"

@Composable
internal fun HeaderText(
    instant: Instant,
    period: ForecastPeriod,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
    textAlign: TextAlign = TextAlign.Center,
    buttonVariant: ButtonVariant = ButtonVariant.PrimaryElevated,
    style: TextStyle = AppTheme.typography.h1,
) {
    val buttonText = period.rememberText(instant)

    val textLength = measureTextWidth(buttonText)
    val inlineContent = remember(buttonText) {
        mapOf(
            INLINE_CONTENT_ID to InlineTextContent(
                Placeholder(
                    width = textLength,
                    height = style.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                ),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .wrapContentSize(unbounded = true),
                ) {
                    Button(
                        onClick = onClick,
                        shape = AppTheme.shapes.extraSmall,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        variant = buttonVariant,
                    ) {
                        Text(
                            text = buttonText,
                            style = AppTheme.typography.h2,
                        )
                    }
                }
            },
        )
    }

    Text(
        text = buildAnnotatedString {
            append(Res.string.forecast_title.get())
            appendInlineContent(INLINE_CONTENT_ID, "[button]")
        },
        style = style,
        maxLines = maxLines,
        textAlign = textAlign,
        inlineContent = inlineContent,
        autoSize = TextAutoSize.StepBased(maxFontSize = style.fontSize),
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun measureTextWidth(text: String): TextUnit {
    val textMeasurer = rememberTextMeasurer()
    val buttonStyle = AppTheme.typography.h2
    val density = LocalDensity.current
    val textWidthPx = textMeasurer.measure(text, buttonStyle).size.width
    // 4.dp Box start padding + 8.dp×2 Button horizontal content padding + 4.dp brutal elevation shadow
    val paddingPx = with(density) { 24.dp.toPx() }
    return with(density) { (textWidthPx + paddingPx).toSp() }
}

@Preview
@Composable
private fun HeaderTextPreview() {
    AppPreview {
        Box(Modifier.background(AppTheme.colors.background)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ForecastPeriod.entries.forEach { period ->
                    Surface {
                        HeaderText(
                            instant = LocalDateTime(
                                year = 2025,
                                month = 5,
                                day = 15,
                                hour = 15,
                                minute = 30,
                            ).toInstant(TimeZone.currentSystemDefault()),
                            period = period,
                            onClick = {},
                        )
                    }
                }
            }
        }
    }
}
