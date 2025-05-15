package app.sigot.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface AppStartDestination {
    @Serializable
    data object Onboarding : AppStartDestination

    @Serializable
    data object Home : AppStartDestination

    // fun toRoute(): KClass<*> =
    //     when (this) {
    //         Onboarding -> OnboardingRoute::class
    //         Home -> HomeRoute::class
    //     }
}
