package app.sigot.di

import app.sigot.core.domain.forecast.VisualCrossingTokenProvider
import app.sigot.data.forecast.AppForecastSource
import app.sigot.data.forecast.AppTokenProvider
import app.sigot.data.forecast.BackendForecastSource
import app.sigot.forecast.data.source.ForecastSource
import app.sigot.forecast.data.source.QueryCostLogger
import app.sigot.forecast.directApiFortuneSource
import app.sigot.ui.uiModule
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun appModule() =
    module {
        includes(uiModule())

        factory<QueryCostLogger> { QueryCostLogger {} }
        factoryOf(::AppTokenProvider) bind VisualCrossingTokenProvider::class
        factoryOf(::BackendForecastSource) bind ForecastSource::class
        factory {
            val directSource = directApiFortuneSource()
            AppForecastSource(
                settingsRepo = get(),
                backendSource = get<BackendForecastSource>(),
                directSource = directSource,
            )
        } bind ForecastSource::class
    }
