package now.shouldigooutside.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import now.shouldigooutside.core.platform.Platform
import now.shouldigooutside.core.platform.platform
import now.shouldigooutside.forecast.ui.navigation.forecastNavigation
import now.shouldigooutside.onboarding.ui.navigation.OnboardingRoute
import now.shouldigooutside.onboarding.ui.navigation.onboardingNavigation
import now.shouldigooutside.settings.ui.navigation.settingsNavigation
import now.shouldigooutside.ui.home.navigation.HomeRoute
import now.shouldigooutside.ui.home.navigation.homeScreen
import now.shouldigooutside.webview.navigation.WebViewRoute
import now.shouldigooutside.webview.navigation.webViewNavigation
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
            onFinish = {
                navController.navigate(HomeRoute) {
                    popUpTo(HomeRoute)
                }
            },
        )

        homeScreen(navController)

        forecastNavigation(navController)

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
internal fun AppNavHost(
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
