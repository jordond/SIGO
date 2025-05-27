package app.sigot.settings.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import app.sigot.core.ui.navigation.Route
import app.sigot.core.ui.navigation.bottomsheet.BottomSheetNavigator
import app.sigot.core.ui.navigation.bottomsheet.bottomSheet
import app.sigot.core.ui.navigation.popUpScreen
import app.sigot.core.ui.navigation.slideHorizontally
import app.sigot.settings.ui.SettingsScreen
import app.sigot.settings.ui.internal.InternalSettingsScreen
import app.sigot.settings.ui.preferences.PreferencesScreen
import app.sigot.settings.ui.units.UnitsScreen
import kotlinx.serialization.Serializable

@Serializable
public data object SettingsRoute : Route

@Serializable
internal data object InternalSettingsRoute : Route

@Serializable
internal data object UnitsRoute : Route

@Serializable
internal data object PreferencesRoute : Route

@Serializable
public data object PreferencesPopUpRoute : Route

public fun NavGraphBuilder.settingsNavigation(
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    toWebView: (title: String, url: String) -> Unit,
) {
    popUpScreen<SettingsRoute> {
        SettingsScreen(
            onBack = navController::popBackStack,
            toInternalSettings = { navController.navigate(InternalSettingsRoute) },
            toUnits = { navController.navigate(UnitsRoute) },
            toPreferences = { navController.navigate(PreferencesRoute) },
            toWebView = toWebView,
        )
    }

    slideHorizontally<InternalSettingsRoute> {
        InternalSettingsScreen()
    }

    slideHorizontally<UnitsRoute> { entry ->
        UnitsScreen(
            onBack = navController::popBackStack,
        )
    }

    slideHorizontally<PreferencesRoute> { entry ->
        PreferencesScreen(
            onBack = navController::popBackStack,
            isBack = true,
        )
    }

    bottomSheet<PreferencesPopUpRoute> { entry ->
        PreferencesScreen(
            onBack = navController::popBackStack,
            isBack = false,
        )
    }
}
