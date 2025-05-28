package app.sigot.forecast.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.sigot.forecast.ui.ForecastHomeScreen
import kotlinx.serialization.Serializable

@Serializable
public data object ForecastHomeRoute

public fun NavGraphBuilder.forecastNavigation(
    toPreferences: () -> Unit,
    toSettings: () -> Unit,
) {
    composable<ForecastHomeRoute> {
        ForecastHomeScreen(
            toViewDetails = {
                // TODO : Implement details screen/sheet
            },
            toPreferences = toPreferences,
            toSettings = toSettings,
        )
    }
}
