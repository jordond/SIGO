package app.sigot.onboarding.ui

import dev.stateholder.extensions.viewmodel.UiStateViewModel

internal class OnboardingModel : UiStateViewModel<OnboardingModel.State, OnboardingModel.Event>(State()) {
    data class State(
        val foo: Boolean = false,
    )

    sealed interface Event
}
