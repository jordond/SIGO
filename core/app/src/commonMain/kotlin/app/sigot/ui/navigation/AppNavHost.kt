package app.sigot.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import app.sigot.core.platform.Platform
import app.sigot.core.platform.platform
import app.sigot.forecast.ui.navigation.ForecastHomeRoute
import app.sigot.forecast.ui.navigation.forecastNavigation
import app.sigot.onboarding.ui.navigation.OnboardingRoute
import app.sigot.onboarding.ui.navigation.onboardingNavigation
import app.sigot.settings.ui.navigation.PreferencesBottomSheet
import app.sigot.settings.ui.navigation.SettingsRoute
import app.sigot.settings.ui.navigation.settingsNavigation
import app.sigot.webview.navigation.WebViewRoute
import app.sigot.webview.navigation.webViewNavigation
import kotlin.reflect.KClass

@Composable
internal fun AppNavHost(
    navController: NavHostController,
    startDestination: AppStartDestination,
    modifier: Modifier = Modifier,
) {
    AppNavHost(
        navController = navController,
        startDestination = remember(startDestination) { startDestination.toRoute() },
        modifier = modifier,
    ) {
        onboardingNavigation(
            navController = navController,
            onFinish = { navController.navigate(ForecastHomeRoute) },
        )

        forecastNavigation(
            navController = navController,
            toPreferences = { navController.navigate(PreferencesBottomSheet) },
            toSettings = { navController.navigate(SettingsRoute) },
        )

        settingsNavigation(
            navController = navController,
            toWebView = { title, url ->
                navController.navigate(WebViewRoute(title, url))
            },
            toOnboarding = { navController.navigate(OnboardingRoute) },
        )

        webViewNavigation(onBack = navController::popBackStack)
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    startDestination: KClass<*>,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit,
) {
    if (platform == Platform.iOS) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(2000)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(300)) },
            builder = builder,
        )
    } else {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier,
            builder = builder,
        )
    }
}
