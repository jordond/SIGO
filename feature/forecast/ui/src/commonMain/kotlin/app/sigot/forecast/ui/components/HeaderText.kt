package app.sigot.forecast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_title
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.Surface
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.mappers.rememberText
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.compose.ui.tooling.preview.Preview

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

    val textLength = measureTextWidth(buttonText, style)
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
                    modifier = Modifier.padding(start = 4.dp),
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
private fun measureTextWidth(
    text: String,
    style: TextStyle,
): TextUnit {
    val textMeasurer = rememberTextMeasurer()
    val percent = remember(text) {
        when (text.length) {
            in 1..5 -> 0.95f
            in 6..10 -> 0.85f
            else -> 0.8f
        }
    }
    val widthInPixels = textMeasurer.measure(text, style).size.width * percent
    return with(LocalDensity.current) { widthInPixels.toSp() }
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
                                monthNumber = 5,
                                dayOfMonth = 15,
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
