package now.shouldigooutside.settings.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.ui.navigation.Route
import now.shouldigooutside.core.ui.navigation.bottomsheet.bottomSheet
import now.shouldigooutside.core.ui.navigation.popUpScreen
import now.shouldigooutside.core.ui.navigation.slideHorizontally
import now.shouldigooutside.settings.ui.SettingsScreen
import now.shouldigooutside.settings.ui.internal.InternalSettingsScreen
import now.shouldigooutside.settings.ui.preferences.PreferencesBottomSheet
import now.shouldigooutside.settings.ui.preferences.PreferencesScreen
import now.shouldigooutside.settings.ui.units.UnitsScreen

@Serializable
public data object SettingsRoute : Route

@Serializable
internal data object InternalSettingsRoute : Route

@Serializable
internal data object UnitsRoute : Route

@Serializable
internal data object PreferencesRoute : Route

@Serializable
public data object PreferencesBottomSheet : Route

public fun NavGraphBuilder.settingsNavigation(
    navController: NavHostController,
    toWebView: (title: String, url: String) -> Unit,
    toOnboarding: () -> Unit = {},
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
        InternalSettingsScreen(
            onBack = navController::popBackStack,
            onLaunchOnboarding = toOnboarding,
        )
    }

    slideHorizontally<UnitsRoute> {
        UnitsScreen(onBack = navController::popBackStack)
    }

    slideHorizontally<PreferencesRoute> {
        PreferencesScreen(onBack = navController::popBackStack)
    }

    bottomSheet<PreferencesBottomSheet> {
        PreferencesBottomSheet(onBack = navController::popBackStack)
    }
}
