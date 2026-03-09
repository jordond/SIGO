package app.sigot.forecast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.sigot.core.ui.navigation.Route
import app.sigot.core.ui.navigation.popUpScreen
import app.sigot.forecast.ui.ForecastDetailsScreen
import app.sigot.forecast.ui.ForecastHomeScreen
import kotlinx.serialization.Serializable

@Serializable
public data object ForecastHomeRoute

@Serializable
internal data object ForecastDetailsRoute : Route

public fun NavGraphBuilder.forecastNavigation(
    navController: NavController,
    toPreferences: () -> Unit,
    toSettings: () -> Unit,
) {
    composable<ForecastHomeRoute> {
        ForecastHomeScreen(
            toViewDetails = { navController.navigate(ForecastDetailsRoute) },
            toPreferences = toPreferences,
            toSettings = toSettings,
        )
    }

    popUpScreen<ForecastDetailsRoute> {
        ForecastDetailsScreen(
            onBack = navController::popBackStack,
        )
    }
}
