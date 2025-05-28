package app.sigot.forecast.ui.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.resources.Res
import app.sigot.core.resources.preferences
import app.sigot.core.resources.settings
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Settings
import app.sigot.core.ui.icons.lucide.SlidersHorizontal
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun HomeBottomBar(
    toSettings: () -> Unit,
    toPreferences: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
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
            HomeBottomBar({}, {})
        }
        AppPreview(isDarkTheme = true) {
            HomeBottomBar({}, {})
        }
    }
}
