package app.sigot.onboarding.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.sigot.onboarding.ui.location.LocationScreen
import app.sigot.onboarding.ui.preferences.PreferencesScreen
import app.sigot.onboarding.ui.summary.SummaryScreen
import app.sigot.onboarding.ui.units.UnitsScreen
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
        composable<OnboardingDestination.Welcome> {
            WelcomeScreen()
        }

        composable<OnboardingDestination.Units> {
            UnitsScreen()
        }

        composable<OnboardingDestination.Preferences> {
            PreferencesScreen()
        }

        composable<OnboardingDestination.Location> {
            LocationScreen()
        }

        composable<OnboardingDestination.Summary> {
            SummaryScreen()
        }
    }
}
