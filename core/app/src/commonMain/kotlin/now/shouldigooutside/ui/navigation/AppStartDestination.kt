package now.shouldigooutside.ui.navigation

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import now.shouldigooutside.forecast.ui.navigation.ForecastHomeRoute
import now.shouldigooutside.onboarding.ui.navigation.OnboardingRoute
import now.shouldigooutside.ui.home.navigation.HomeRoute
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
            Home -> HomeRoute::class
        }
}
