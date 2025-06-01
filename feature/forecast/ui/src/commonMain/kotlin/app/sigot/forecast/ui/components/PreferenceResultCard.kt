package app.sigot.forecast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.BrutalColors
import app.sigot.core.ui.brutal
import app.sigot.core.ui.cardColors
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun PreferenceResultCard(
    title: String,
    text: String,
    colors: BrutalColors,
    value: @Composable () -> String,
    modifier: Modifier = Modifier,
    height: Dp = 100.dp,
) {
    Card(
        colors = colors.cardColors(),
        modifier = modifier.height(height),
    ) {
        Column(
            modifier = Modifier.widthIn(max = 150.dp).fillMaxWidth(),
        ) {
            Text(
                text = title,
                maxLines = 1,
                style = AppTheme.typography.h4,
                textAlign = TextAlign.Center,
                autoSize = TextAutoSize.StepBased(maxFontSize = AppTheme.typography.h4.fontSize),
                modifier = Modifier
                    .background(colors.bright)
                    .padding(top = 4.dp, bottom = 2.dp)
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
            )

            HorizontalDivider()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = value(),
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 18.sp),
                    style = AppTheme.typography.body1.copy(fontSize = 18.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 2.dp),
                )
                HorizontalDivider()
                Text(
                    text = text,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 32.sp),
                    style = AppTheme.typography.h2.copy(fontSize = 32.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreferenceResultCardPreview() {
    AppPreview {
        PreferenceResultCard(
            title = "Temp",
            text = "Too Hot",
            colors = AppTheme.colors.brutal.orange,
            value = { "30°C" },
            modifier = Modifier.padding(16.dp),
        )
    }
}
