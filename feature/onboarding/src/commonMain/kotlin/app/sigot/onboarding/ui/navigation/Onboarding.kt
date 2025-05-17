package app.sigot.onboarding.ui.navigation

import app.sigot.core.ui.navigation.Route
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface Onboarding : Route {
    data object WelcomeRoute : Onboarding

    data object UnitsRoute : Onboarding

    data object PreferencesRoute : Onboarding

    data object LocationRoute : Onboarding

    data object SummaryRoute : Onboarding
}
