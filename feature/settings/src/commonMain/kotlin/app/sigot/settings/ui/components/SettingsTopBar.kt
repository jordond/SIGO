package app.sigot.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.resources.Res
import app.sigot.core.resources.back
import app.sigot.core.resources.close
import app.sigot.core.resources.settings
import app.sigot.core.resources.settings_internal_title
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.LocalContentColor
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.ArrowBigDown
import app.sigot.core.ui.icons.lucide.ArrowLeft
import app.sigot.core.ui.icons.lucide.X
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

internal enum class SettingsTopBarNav {
    Back,
    Close,
    Down,
}

@Composable
internal fun SettingsTopBar(
    text: StringResource,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    handleInsets: Boolean = true,
    navType: SettingsTopBarNav = SettingsTopBarNav.Close,
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = if (handleInsets) TopAppBarDefaults.windowInsets else WindowInsets(0.dp),
        title = {
            Text(
                text = text.get(),
                style = AppTheme.typography.h1,
                autoSize = TextAutoSize.StepBased(maxFontSize = AppTheme.typography.h1.fontSize),
                modifier = Modifier.padding(start = 8.dp),
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                variant = IconButtonVariant.Outlined,
            ) {
                val icon = remember(navType) {
                    when (navType) {
                        SettingsTopBarNav.Back -> AppIcons.Lucide.ArrowLeft
                        SettingsTopBarNav.Close -> AppIcons.Lucide.X
                        SettingsTopBarNav.Down -> AppIcons.Lucide.ArrowBigDown
                    }
                }
                val description = remember {
                    if (navType == SettingsTopBarNav.Back) Res.string.back else Res.string.close
                }
                Icon(
                    icon = icon,
                    contentDescription = description.get(),
                )
            }
        },
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = LocalContainerColor.current,
            navigationIconContentColor = LocalContentColor.current,
            titleContentColor = LocalContentColor.current,
        ),
    )
}

@Preview
@Composable
private fun SettingsTopBarPreview() {
    AppPreview(useSurface = false) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SettingsTopBar(
                text = Res.string.settings,
                onBack = {},
            )
            SettingsTopBar(
                text = Res.string.settings_internal_title,
                onBack = {},
                navType = SettingsTopBarNav.Back,
            )
            SettingsTopBar(
                text = Res.string.settings,
                onBack = {},
                navType = SettingsTopBarNav.Down,
            )
        }
    }
}
