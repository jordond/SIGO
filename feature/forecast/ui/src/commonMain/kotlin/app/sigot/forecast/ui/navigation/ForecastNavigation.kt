package app.sigot.forecast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.ui.navigation.Route
import app.sigot.core.ui.navigation.popUpScreen
import app.sigot.forecast.ui.ForecastHomeScreen
import app.sigot.forecast.ui.details.ForecastDetailsScreen
import kotlinx.serialization.Serializable

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
