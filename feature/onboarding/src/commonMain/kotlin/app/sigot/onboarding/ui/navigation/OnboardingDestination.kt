package app.sigot.onboarding.ui.navigation

import androidx.compose.runtime.Immutable
import app.sigot.core.ui.navigation.Route
import kotlinx.serialization.Serializable

@Immutable
@Serializable
internal sealed interface OnboardingDestination : Route {
    @Serializable
    data object Welcome : OnboardingDestination

    @Serializable
    data object Units : OnboardingDestination

    @Serializable
    data object Preferences : OnboardingDestination

    @Serializable
    data object Location : OnboardingDestination

    @Serializable
    data object Summary : OnboardingDestination
}
