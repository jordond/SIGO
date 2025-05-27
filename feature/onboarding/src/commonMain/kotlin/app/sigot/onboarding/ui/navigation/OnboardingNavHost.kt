package app.sigot.onboarding.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import app.sigot.core.ui.navigation.slideHorizontally
import app.sigot.onboarding.ui.location.LocationScreen
import app.sigot.onboarding.ui.preferences.OnboardingPreferencesScreen
import app.sigot.onboarding.ui.summary.SummaryScreen
import app.sigot.onboarding.ui.units.OnboardingUnitsScreen
import app.sigot.onboarding.ui.welcome.WelcomeScreen

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

        slideHorizontally<OnboardingDestination.Units> {
            OnboardingUnitsScreen()
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
