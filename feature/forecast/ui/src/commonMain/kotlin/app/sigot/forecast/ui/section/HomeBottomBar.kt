package app.sigot.forecast.ui.section

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_view_details
import app.sigot.core.resources.preferences
import app.sigot.core.resources.settings
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Settings
import app.sigot.core.ui.icons.lucide.SlidersHorizontal
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview

@Composable
internal fun HomeBottomBar(
    canGoToDetails: Boolean,
    toDetails: () -> Unit,
    toSettings: () -> Unit,
    toPreferences: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp, top = 8.dp),
    ) {
        IconButton(
            variant = IconButtonVariant.SecondaryElevated,
            onClick = toSettings,
        ) {
            Icon(
                icon = AppIcons.Lucide.Settings,
                contentDescription = Res.string.settings.get(),
            )
        }

        val animatedAlpha by animateFloatAsState(
            targetValue = if (canGoToDetails) 1f else 0f,
            label = "details_button_alpha",
        )
        Box(
            modifier = Modifier
                .graphicsLayer {
                    this.alpha = animatedAlpha
                }.padding(horizontal = AppTheme.spacing.small)
                .weight(1f),
        ) {
            Button(
                variant = ButtonVariant.PrimaryElevated,
                onClick = toDetails,
            ) {
                Text(
                    text = Res.string.forecast_view_details.get(),
                    style = AppTheme.typography.h2,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    autoSize = TextAutoSize.StepBased(maxFontSize = AppTheme.typography.h2.fontSize),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        IconButton(
            variant = IconButtonVariant.SecondaryElevated,
            onClick = toPreferences,
        ) {
            Icon(
                icon = AppIcons.Lucide.SlidersHorizontal,
                contentDescription = Res.string.preferences.get(),
            )
        }
    }
}

@Preview
@Composable
private fun HomeBottomBarPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier
            .padding(16.dp)
            .background(AppTheme.colors.surface),
    ) {
        AppPreview {
            HomeBottomBar(true, {}, {}, {})
            HomeBottomBar(false, {}, {}, {})
        }
        AppPreview(isDarkTheme = true) {
            HomeBottomBar(true, {}, {}, {})
            HomeBottomBar(false, {}, {}, {})
        }
    }
}
