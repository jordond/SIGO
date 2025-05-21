package app.sigot.onboarding

import app.sigot.onboarding.ui.OnboardingModel
import app.sigot.onboarding.ui.location.LocationModel
import app.sigot.onboarding.ui.preferences.PreferencesModel
import app.sigot.onboarding.ui.units.UnitsModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public fun onboardingModule(): Module =
    module {
        viewModelOf(::OnboardingModel)
        viewModelOf(::UnitsModel)
        viewModelOf(::PreferencesModel)
        viewModelOf(::LocationModel)
    }
