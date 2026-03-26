package now.shouldigooutside.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.home_tab_activities
import now.shouldigooutside.core.resources.settings
import now.shouldigooutside.core.ui.components.topbar.TopBar
import now.shouldigooutside.core.ui.components.topbar.TopBarDefaults
import now.shouldigooutside.core.ui.components.topbar.TopBarScrollBehavior
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Settings
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import org.jetbrains.compose.resources.StringResource

@Composable
public fun TabHeader(
    title: StringResource,
    toSettings: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopBarScrollBehavior? = null,
) {
    TopBar(
        colors = TopBarDefaults.topBarColors(
            containerColor = LocalContainerColor.current,
        ),
        scrollBehavior = scrollBehavior,
        title = {
            Title(text = title.get())
        },
        actions = {
            Action(
                icon = AppIcons.Lucide.Settings,
                contentDescription = Res.string.settings.get(),
                onClick = toSettings,
            )
        },
        modifier = modifier,
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        TabHeader(Res.string.home_tab_activities, toSettings = {})
    }
}
