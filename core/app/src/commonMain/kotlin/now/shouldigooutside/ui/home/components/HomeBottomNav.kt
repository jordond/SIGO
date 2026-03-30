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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import now.shouldigooutside.core.ui.LocalAppExperience
import now.shouldigooutside.core.ui.LocalTextStyle
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.NavigationBar
import now.shouldigooutside.core.ui.components.NavigationBarItem
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.bottombar.BottomBarScrollBehavior
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.ui.home.navigation.HomeTab
import now.shouldigooutside.ui.home.navigation.route

@Composable
internal fun HomeBottomNav(
    selected: HomeTab,
    onClick: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: BottomBarScrollBehavior? = null,
) {
    val enableActivities = LocalAppExperience.current.enableActivities
    val entries = remember(enableActivities) {
        if (enableActivities) {
            HomeTab.entries.toList()
        } else {
            HomeTab.entries.filterNot { it == HomeTab.Activities }
        }
    }
    NavigationBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    ) {
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

public fun NavHostController.navigateHomeTab(tab: HomeTab) {
    navigate(tab.route) {
        val popRoute = graph.findStartDestination().route ?: error("No start destination found")
        popUpTo(route = popRoute) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
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
