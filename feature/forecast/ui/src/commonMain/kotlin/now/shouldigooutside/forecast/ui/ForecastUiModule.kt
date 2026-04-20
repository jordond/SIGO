package now.shouldigooutside.forecast.ui

import now.shouldigooutside.forecast.ui.activities.ActivitiesModel
import now.shouldigooutside.forecast.ui.activities.add.AddActivityModel
import now.shouldigooutside.forecast.ui.components.AlertsBottomSheetModel
import now.shouldigooutside.forecast.ui.forecast.ForecastHomeModel
import now.shouldigooutside.forecast.ui.forecast.details.ForecastDetailsModel
import now.shouldigooutside.forecast.ui.location.LocationSearchModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public fun forecastUiModule(): Module =
    module {
        viewModelOf(::ForecastHomeModel)
        viewModelOf(::ForecastDetailsModel)
        viewModelOf(::ActivitiesModel)
        viewModelOf(::AddActivityModel)
        viewModelOf(::LocationSearchModel)
        viewModelOf(::AlertsBottomSheetModel)
    }
