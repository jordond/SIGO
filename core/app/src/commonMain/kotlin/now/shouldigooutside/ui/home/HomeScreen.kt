package now.shouldigooutside.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.stateholder.dispatcher.rememberDispatcher
import now.shouldigooutside.core.model.forecast.SevereWeatherRisk
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.ui.components.Scaffold
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import now.shouldigooutside.forecast.ui.forecast.ForecastHomeScreen
import now.shouldigooutside.settings.ui.preferences.PreferencesTab
import now.shouldigooutside.ui.home.components.HomeBottomNav
import now.shouldigooutside.ui.home.navigation.HomeScreenNavHost
import now.shouldigooutside.ui.home.navigation.HomeTab
import now.shouldigooutside.ui.home.navigation.route
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
            current?.destination?.hasRoute(entry.route) == true
        } ?: HomeTab.Forecast
    }

    HomeScreen(
        selected = currentHomeTab,
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
    tabContent: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
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
            tabContent()
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    var selected by remember { mutableStateOf(HomeTab.Forecast) }
    AppPreview {
        HomeScreen(
            selected = HomeTab.Forecast,
            onTabClick = {},
            tabContent = {
                when (selected) {
                    HomeTab.Forecast -> {
                        val forecast = PreviewData.Forecast.createForecastFrom(
                            PreviewData.Forecast.severeWeather(SevereWeatherRisk.Low),
                        )
                        ForecastHomeScreen(
                            location = PreviewData.location,
                            preferences = Preferences.default,
                            units = Units.Metric,
                            data = PreviewData.Forecast.forecastData(forecast),
                            dispatcher = rememberDispatcher { },
                        )
                    }
                    HomeTab.Activities -> {
                        TODO()
                    }
                    HomeTab.Preferences -> {
                        PreferencesTab(
                            units = Units.Metric,
                            preferences = Preferences.default,
                            update = {},
                        )
                    }
                }
            },
        )
    }
}
