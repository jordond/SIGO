package now.shouldigooutside.forecast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.ui.navigation.Route
import now.shouldigooutside.core.ui.navigation.popUpScreen
import now.shouldigooutside.forecast.ui.ForecastHomeScreen
import now.shouldigooutside.forecast.ui.details.ForecastDetailsScreen

@Serializable
public data object ForecastHomeRoute

@Serializable
internal class ForecastDetailsRoute private constructor(
    private val periodString: String,
) : Route {
    constructor(period: ForecastPeriod) : this(periodString = period.name)

    val period: ForecastPeriod
        get() = ForecastPeriod.valueOf(periodString)
}

public fun NavGraphBuilder.forecastNavigation(
    navController: NavController,
    toPreferences: () -> Unit,
    toSettings: () -> Unit,
) {
    composable<ForecastHomeRoute> {
        ForecastHomeScreen(
            toViewDetails = { navController.navigate(ForecastDetailsRoute(it)) },
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
