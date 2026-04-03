package now.shouldigooutside.onboarding

import now.shouldigooutside.onboarding.ui.OnboardingModel
import now.shouldigooutside.onboarding.ui.activities.OnboardingActivitiesModel
import now.shouldigooutside.onboarding.ui.location.LocationModel
import now.shouldigooutside.onboarding.ui.preferences.OnboardingPreferencesModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public fun onboardingModule(): Module =
    module {
        viewModelOf(::OnboardingModel)
        viewModelOf(::OnboardingPreferencesModel)
        viewModelOf(::OnboardingActivitiesModel)
        viewModelOf(::LocationModel)
    }
