package app.sigot.onboarding.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import app.sigot.onboarding.ui.OnboardingScreen
import kotlinx.serialization.Serializable

@Serializable
public data object OnboardingRoute

public fun NavGraphBuilder.onboardingNavigation(
    navController: NavHostController,
    onFinish: () -> Unit,
) {
    composable<OnboardingRoute> {
        OnboardingScreen(
            parentNavController = navController,
            onFinish = onFinish,
        )
    }
}
