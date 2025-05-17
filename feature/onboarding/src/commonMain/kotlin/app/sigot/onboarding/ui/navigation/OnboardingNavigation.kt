package app.sigot.onboarding.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import app.sigot.onboarding.ui.OnboardingScreen
import kotlinx.serialization.Serializable

@Serializable
public data object OnboardingRoute

public fun NavGraphBuilder.onboardingScreen(navController: NavHostController) {
    composable<OnboardingRoute> {
        OnboardingScreen(
            parentNavController = navController,
            onFinish = { /* TODO: Implement onFinish */ },
        )
    }
}
