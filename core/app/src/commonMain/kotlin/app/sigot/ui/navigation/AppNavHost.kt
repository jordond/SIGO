package app.sigot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.sigot.onboarding.ui.navigation.onboardingScreen

@Composable
internal fun AppNavHost(
    startDestination: AppStartDestination,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = remember(startDestination) { startDestination.toRoute() },
        modifier = modifier,
    ) {
        onboardingScreen(navController)
    }
}
