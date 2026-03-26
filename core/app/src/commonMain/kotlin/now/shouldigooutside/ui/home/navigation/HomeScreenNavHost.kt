package now.shouldigooutside.ui.home.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import now.shouldigooutside.core.ui.navigation.Route
import now.shouldigooutside.forecast.ui.navigation.ActivitiesRoute
import now.shouldigooutside.forecast.ui.navigation.ForecastHomeRoute
import now.shouldigooutside.forecast.ui.navigation.forecastNavigation
import now.shouldigooutside.settings.ui.navigation.PreferencesTabRoute
import now.shouldigooutside.settings.ui.preferences.tab.preferencesTab
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
        startDestination = HomeTab.Forecast.routeClass,
    ) {
        forecastNavigation(
            navController = parent,
        )
        preferencesTab(
            toAddActivity = {
                // TODO: Implement add activity flow
            },
        )
    }
}

internal val HomeTab.route: Route
    get() = when (this) {
        HomeTab.Forecast -> ForecastHomeRoute
        HomeTab.Activities -> ActivitiesRoute
        HomeTab.Preferences -> PreferencesTabRoute
    }

internal val HomeTab.routeClass: KClass<*>
    get() = when (this) {
        HomeTab.Forecast -> ForecastHomeRoute::class
        HomeTab.Activities -> ActivitiesRoute::class
        HomeTab.Preferences -> PreferencesTabRoute::class
    }
