package now.shouldigooutside.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.back
import now.shouldigooutside.core.resources.close
import now.shouldigooutside.core.resources.settings
import now.shouldigooutside.core.resources.settings_internal_title
import now.shouldigooutside.core.ui.LocalContainerColor
import now.shouldigooutside.core.ui.components.topbar.TopBar
import now.shouldigooutside.core.ui.components.topbar.TopBarDefaults
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.ArrowBigDown
import now.shouldigooutside.core.ui.icons.lucide.ArrowLeft
import now.shouldigooutside.core.ui.icons.lucide.X
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import org.jetbrains.compose.resources.StringResource

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
    TopBar(
        modifier = modifier,
        windowInsets = if (handleInsets) TopBarDefaults.windowInsets else WindowInsets(0.dp),
        title = {
            Title(text = text.get())
        },
        navigationIcon = {
            val icon = remember(navType) {
                when (navType) {
                    SettingsTopBarNav.Back -> AppIcons.Lucide.ArrowLeft
                    SettingsTopBarNav.Close -> AppIcons.Lucide.X
                    SettingsTopBarNav.Down -> AppIcons.Lucide.ArrowBigDown
                }
            }
            val description = remember(navType) {
                if (navType == SettingsTopBarNav.Back) Res.string.back else Res.string.close
            }
            NavIcon(
                icon = icon,
                contentDescription = description.get(),
                onClick = onBack,
            )
        },
        colors = TopBarDefaults.topBarColors(
            containerColor = LocalContainerColor.current,
        ),
    )
}

@Preview
@Composable
private fun SettingsTopBarPreview() {
    AppPreview {
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
