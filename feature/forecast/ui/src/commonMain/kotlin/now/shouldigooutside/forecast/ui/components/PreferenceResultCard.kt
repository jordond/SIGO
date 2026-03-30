package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.Card
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Droplet
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun PreferenceResultCard(
    title: String,
    text: String,
    colors: BrutalColors,
    value: @Composable () -> String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp? = 100.dp,
) {
    Card(
        colors = colors.cardColors(),
        modifier = modifier
            .then(if (height != null) Modifier.height(height) else Modifier)
            .widthIn(min = 150.dp, max = 150.dp)
            .width(IntrinsicSize.Min),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .background(colors.bright)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                ) {
                    Icon(
                        icon = icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterStart),
                    )
                    Text(
                        text = title,
                        maxLines = 1,
                        style = AppTheme.typography.h4,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            } else {
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
            }

            HorizontalDivider()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = text,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 32.sp),
                    style = AppTheme.typography.h2.copy(fontSize = 32.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )

                val valueString = value()
                if (valueString.isNotBlank()) {
                    HorizontalDivider()
                }

                Text(
                    text = valueString,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 16.sp),
                    style = AppTheme.typography.body1.copy(fontSize = 16.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 2.dp, top = 1.dp),
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

@Preview
@Composable
private fun PreferenceResultCardWithIconPreview() {
    AppPreview {
        PreferenceResultCard(
            title = "Wind",
            text = "9km/h",
            colors = AppTheme.colors.brutal.blue,
            value = { "Gust 18 km/h" },
            icon = AppIcons.Lucide.Droplet,
            modifier = Modifier.padding(16.dp),
        )
    }
}
