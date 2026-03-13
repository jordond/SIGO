package app.sigot.ui.navigation

import androidx.compose.runtime.Immutable
import app.sigot.forecast.ui.navigation.ForecastHomeRoute
import app.sigot.onboarding.ui.navigation.OnboardingRoute
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Immutable
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
