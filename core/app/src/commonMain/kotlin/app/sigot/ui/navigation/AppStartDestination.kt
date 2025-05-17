package app.sigot.ui.navigation

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
            Onboarding -> TODO()
            Home -> TODO()
        }
}
