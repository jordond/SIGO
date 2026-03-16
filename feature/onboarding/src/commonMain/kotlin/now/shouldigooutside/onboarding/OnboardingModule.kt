package now.shouldigooutside.onboarding

import now.shouldigooutside.onboarding.ui.OnboardingModel
import now.shouldigooutside.onboarding.ui.location.LocationModel
import now.shouldigooutside.onboarding.ui.preferences.OnboardingPreferencesModel
import now.shouldigooutside.onboarding.ui.units.OnboardingUnitsModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public fun onboardingModule(): Module =
    module {
        viewModelOf(::OnboardingModel)
        viewModelOf(::OnboardingUnitsModel)
        viewModelOf(::OnboardingPreferencesModel)
        viewModelOf(::LocationModel)
    }
