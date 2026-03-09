package app.sigot.forecast.ui

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

public fun forecastUiModule(): Module =
    module {
        viewModelOf(::ForecastHomeModel)
        viewModelOf(::ForecastDetailsModel)
    }
