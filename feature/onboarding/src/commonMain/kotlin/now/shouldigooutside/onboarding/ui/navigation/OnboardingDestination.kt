package now.shouldigooutside.onboarding.ui.navigation

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.ui.navigation.Route

@Immutable
@Serializable
internal sealed interface OnboardingDestination : Route {
    @Serializable
    data object Welcome : OnboardingDestination

    @Serializable
    data object Preferences : OnboardingDestination

    @Serializable
    data object Location : OnboardingDestination

    @Serializable
    data object Summary : OnboardingDestination
}
