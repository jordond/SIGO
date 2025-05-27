package app.sigot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import app.sigot.core.ui.navigation.bottomsheet.BottomSheetNavigator
import app.sigot.forecast.ui.navigation.ForecastHomeRoute
import app.sigot.forecast.ui.navigation.forecastNavigation
import app.sigot.onboarding.ui.navigation.onboardingNavigation
import app.sigot.settings.ui.navigation.PreferencesPopUpRoute
import app.sigot.settings.ui.navigation.SettingsRoute
import app.sigot.settings.ui.navigation.settingsNavigation
import app.sigot.webview.navigation.WebViewRoute
import app.sigot.webview.navigation.webViewNavigation

@Composable
internal fun AppNavHost(
    bottomSheetNavigator: BottomSheetNavigator,
    navController: NavHostController,
    startDestination: AppStartDestination,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = remember(startDestination) { startDestination.toRoute() },
        modifier = modifier,
    ) {
        onboardingNavigation(
            navController = navController,
            onFinish = { navController.navigate(ForecastHomeRoute) },
        )

        forecastNavigation(
            toPreferences = { navController.navigate(PreferencesPopUpRoute) },
            toSettings = { navController.navigate(SettingsRoute) },
        )

        settingsNavigation(
            bottomSheetNavigator = bottomSheetNavigator,
            navController = navController,
            toWebView = { title, url ->
                navController.navigate(WebViewRoute(title, url))
            },
        )

        webViewNavigation(onBack = navController::popBackStack)
    }
}
