package now.shouldigooutside.onboarding.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import now.shouldigooutside.core.ui.navigation.slideHorizontally
import now.shouldigooutside.onboarding.ui.location.LocationScreen
import now.shouldigooutside.onboarding.ui.preferences.OnboardingPreferencesScreen
import now.shouldigooutside.onboarding.ui.summary.SummaryScreen
import now.shouldigooutside.onboarding.ui.welcome.WelcomeScreen

@Composable
internal fun OnboardingNavHost(
    navController: NavHostController,
    parentNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = OnboardingDestination.Welcome,
        modifier = modifier,
    ) {
        slideHorizontally<OnboardingDestination.Welcome> {
            WelcomeScreen()
        }

        slideHorizontally<OnboardingDestination.Preferences> {
            OnboardingPreferencesScreen()
        }

        slideHorizontally<OnboardingDestination.Location> {
            LocationScreen()
        }

        slideHorizontally<OnboardingDestination.Summary> {
            SummaryScreen()
        }
    }
}
