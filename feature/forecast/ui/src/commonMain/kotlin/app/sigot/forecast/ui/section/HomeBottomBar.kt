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
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.brutalBorder
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.rounded
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun HomeBottomBar(
    toSettings: () -> Unit,
    toPreferences: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = AppTheme.shapes.medium.rounded(bottom = false)
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .background(
                color = AppTheme.colors.surface,
                shape = shape,
            ).brutalBorder(shape = shape)
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Button(
            text = Res.string.settings.get(),
            variant = ButtonVariant.SecondaryElevated,
            onClick = toSettings,
        )

        Button(
            text = Res.string.preferences.get(),
            variant = ButtonVariant.SecondaryElevated,
            onClick = toPreferences,
        )
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
