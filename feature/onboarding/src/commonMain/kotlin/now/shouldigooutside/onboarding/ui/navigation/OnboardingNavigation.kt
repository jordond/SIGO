package now.shouldigooutside.onboarding.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import now.shouldigooutside.onboarding.ui.OnboardingScreen

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
