package now.shouldigooutside.ui.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import now.shouldigooutside.core.ui.LocalTextStyle
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.NavigationBar
import now.shouldigooutside.core.ui.components.NavigationBarItem
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.ui.home.navigation.HomeTab

@Composable
internal fun HomeBottomNav(
    selected: HomeTab,
    onClick: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entries = remember { HomeTab.entries.toList() }
    NavigationBar {
        entries.forEach { entry ->
            NavigationBarItem(
                selected = entry == selected,
                icon = {
                    Icon(icon = entry.icon, contentDescription = entry.title.get())
                },
                label = {
                    Text(
                        text = entry.title,
                        autoSize = LocalTextStyle.current.autoSize(),
                        maxLines = 1,
                    )
                },
                onClick = { onClick(entry) },
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    var selected by remember { mutableStateOf(HomeTab.default) }
    AppPreview {
        HomeBottomNav(
            selected = selected,
            onClick = { selected = it },
        )
    }
}
