package app.sigot.settings.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.sigot.core.ui.navigation.Route
import app.sigot.core.ui.navigation.popUpScreen
import app.sigot.core.ui.navigation.slideHorizontally
import app.sigot.settings.ui.SettingsScreen
import app.sigot.settings.ui.internal.InternalSettingsScreen
import kotlinx.serialization.Serializable

@Serializable
public data object SettingsRoute : Route

@Serializable
internal data object InternalSettingsRoute : Route

public fun NavGraphBuilder.settingsNavigation(
    navController: NavHostController,
    toWebView: (title: String, url: String) -> Unit,
) {
    popUpScreen<SettingsRoute> {
        SettingsScreen(
            onBack = navController::popBackStack,
            toInternalSettings = { navController.navigate(InternalSettingsRoute) },
            toUnits = { },
            toPreferences = { },
            toWebView = toWebView,
        )
    }

    slideHorizontally<InternalSettingsRoute> {
        InternalSettingsScreen()
    }
}
