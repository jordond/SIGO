package now.shouldigooutside.forecast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.ui.navigation.Route
import now.shouldigooutside.core.ui.navigation.popUpScreen
import now.shouldigooutside.forecast.ui.activities.ActivitiesTab
import now.shouldigooutside.forecast.ui.activities.add.AddActivityScreen
import now.shouldigooutside.forecast.ui.forecast.ForecastHomeScreen
import now.shouldigooutside.forecast.ui.forecast.details.ForecastDetailsScreen

@Serializable
public data object ForecastHomeRoute : Route

@Serializable
public data object ForecastDetailsRoute : Route

@Serializable
public data object ActivitiesRoute : Route

@Serializable
public data object AddActivityRoute : Route

public fun NavGraphBuilder.forecastTab(
    navController: NavHostController,
    tabNavController: NavHostController,
    toSettings: () -> Unit,
) {
    composable<ForecastHomeRoute> {
        ForecastHomeScreen(
            toViewDetails = { tabNavController.navigate(ForecastDetailsRoute) },
        )
    }

    composable<ForecastDetailsRoute> {
        ForecastDetailsScreen(
            onBack = navController::popBackStack,
            toSettings = toSettings,
        )
    }

    composable<ActivitiesRoute> {
        ActivitiesTab(
            toSettings = toSettings,
            toAddActivity = { navController.navigate(AddActivityRoute) },
        )
    }
}

public fun NavGraphBuilder.forecastNavigation(navController: NavHostController) {
    popUpScreen<AddActivityRoute> {
        AddActivityScreen(
            onBack = navController::popBackStack,
        )
    }
}
