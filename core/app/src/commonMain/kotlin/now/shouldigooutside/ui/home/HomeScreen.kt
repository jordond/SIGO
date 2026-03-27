package now.shouldigooutside.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import now.shouldigooutside.core.ui.components.Scaffold
import now.shouldigooutside.core.ui.components.snackbar.LocalSnackbarProvider
import now.shouldigooutside.core.ui.components.snackbar.Snackbar
import now.shouldigooutside.core.ui.components.snackbar.SnackbarHost
import now.shouldigooutside.core.ui.components.snackbar.SnackbarProvider
import now.shouldigooutside.core.ui.components.snackbar.rememberSnackbarProvider
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.forecast.ui.activities.ActivityTabPreview
import now.shouldigooutside.forecast.ui.forecast.SunnyPreview
import now.shouldigooutside.forecast.ui.forecast.details.ForecastDetailsTabPreview
import now.shouldigooutside.settings.ui.preferences.tab.PreferencesTabPreview
import now.shouldigooutside.ui.home.components.HomeBottomNav
import now.shouldigooutside.ui.home.navigation.HomeScreenNavHost
import now.shouldigooutside.ui.home.navigation.HomeTab
import now.shouldigooutside.ui.home.navigation.route
import now.shouldigooutside.ui.home.navigation.routeClass
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen(
    navController: NavHostController,
    model: HomeModel = koinViewModel(),
) {
    val tabNavController = rememberNavController()
    val current by tabNavController.currentBackStackEntryAsState()
    val currentHomeTab = remember(current) {
        HomeTab.entries.firstOrNull { entry ->
            current?.destination?.hasRoute(entry.routeClass) == true
        } ?: HomeTab.default
    }

    val snackbar = rememberSnackbarProvider()
    HomeScreen(
        selected = currentHomeTab,
        snackbarProvider = snackbar,
        onTabClick = { tab ->
            model.updateSelectedTab(tab)
            tabNavController.navigate(tab.route) {
                val popRoute = (
                    tabNavController.graph.findStartDestination().route
                        ?: error("No start destination found")
                )

                popUpTo(route = popRoute) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
    ) {
        HomeScreenNavHost(
            parent = navController,
            tabNavController = tabNavController,
        )
    }
}

@Composable
internal fun HomeScreen(
    selected: HomeTab,
    onTabClick: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
    snackbarProvider: SnackbarProvider = rememberSnackbarProvider(),
    tabContent: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarProvider.hostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                },
            )
        },
        bottomBar = {
            HomeBottomNav(
                selected = selected,
                onClick = onTabClick,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            CompositionLocalProvider(LocalSnackbarProvider provides snackbarProvider) {
                tabContent()
            }
        }
    }
}

private class Params : PreviewParameterProvider<HomeTab> {
    override val values: Sequence<HomeTab> = HomeTab.entries.asSequence()
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview(
    @PreviewParameter(Params::class) initial: HomeTab,
) {
    var selected by remember { mutableStateOf(initial) }
    AppPreview {
        HomeScreen(
            selected = selected,
            onTabClick = {},
            tabContent = {
                when (selected) {
                    HomeTab.Home -> SunnyPreview()
                    HomeTab.Forecast -> ForecastDetailsTabPreview()
                    HomeTab.Activities -> ActivityTabPreview()
                    HomeTab.Preferences -> PreferencesTabPreview()
                }
            },
        )
    }
}
