package now.shouldigooutside.ui.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import now.shouldigooutside.core.ui.navigation.Route
import now.shouldigooutside.forecast.ui.navigation.ActivitiesRoute
import now.shouldigooutside.forecast.ui.navigation.AddActivityRoute
import now.shouldigooutside.forecast.ui.navigation.ForecastDetailsRoute
import now.shouldigooutside.forecast.ui.navigation.ForecastHomeRoute
import now.shouldigooutside.forecast.ui.navigation.forecastTab
import now.shouldigooutside.settings.ui.navigation.PreferencesTabRoute
import now.shouldigooutside.settings.ui.navigation.SettingsRoute
import now.shouldigooutside.settings.ui.preferences.tab.preferencesTab
import now.shouldigooutside.ui.home.components.navigateHomeTab
import now.shouldigooutside.ui.navigation.AppNavHost
import kotlin.reflect.KClass

@Composable
internal fun HomeScreenNavHost(
    parent: NavHostController,
    tabNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    AppNavHost(
        navController = tabNavController,
        startDestination = HomeTab.default.routeClass,
    ) {
        forecastTab(
            navController = parent,
            tabNavController = tabNavController,
            toSettings = { parent.navigate(SettingsRoute) },
            toHome = { tabNavController.navigateHomeTab(HomeTab.Home) },
        )

        preferencesTab(
            toAddActivity = { parent.navigate(AddActivityRoute) },
            toSettings = { parent.navigate(SettingsRoute) },
        )
    }
}

internal val HomeTab.route: Route
    get() = when (this) {
        HomeTab.Home -> ForecastHomeRoute
        HomeTab.Forecast -> ForecastDetailsRoute
        HomeTab.Activities -> ActivitiesRoute
        HomeTab.Preferences -> PreferencesTabRoute
    }

internal val HomeTab.routeClass: KClass<*>
    get() = when (this) {
        HomeTab.Home -> ForecastHomeRoute::class
        HomeTab.Forecast -> ForecastDetailsRoute::class
        HomeTab.Activities -> ActivitiesRoute::class
        HomeTab.Preferences -> PreferencesTabRoute::class
    }
