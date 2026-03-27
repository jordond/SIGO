package now.shouldigooutside.forecast.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
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
public class ForecastDetailsRoute private constructor(
    private val periodString: String = ForecastPeriod.Now.name,
) : Route {
    public constructor(period: ForecastPeriod = ForecastPeriod.Now) : this(periodString = period.name)

    public val period: ForecastPeriod
        get() = ForecastPeriod.valueOf(periodString)
}

@Serializable
public data object ActivitiesRoute : Route

@Serializable
public data object AddActivityRoute : Route

public fun NavGraphBuilder.forecastNavigation(
    navController: NavController,
    toSettings: () -> Unit,
) {
    composable<ForecastHomeRoute> {
        ForecastHomeScreen(
            toViewDetails = { navController.navigate(ForecastDetailsRoute(it)) },
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
        )
    }

    popUpScreen<AddActivityRoute> {
        AddActivityScreen(
            onBack = navController::popBackStack,
        )
    }
}
