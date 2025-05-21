package app.sigot.ui.navigation

import app.sigot.forecast.ui.navigation.ForecastHomeRoute
import app.sigot.onboarding.ui.navigation.OnboardingRoute
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
internal sealed interface AppStartDestination {
    @Serializable
    data object Onboarding : AppStartDestination

    @Serializable
    data object Home : AppStartDestination

    fun toRoute(): KClass<*> =
        when (this) {
            Onboarding -> OnboardingRoute::class
            Home -> ForecastHomeRoute::class
        }
}
