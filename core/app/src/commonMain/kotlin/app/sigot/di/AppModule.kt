package app.sigot.di

import app.sigot.core.api.client.ApiUrlProvider
import app.sigot.core.domain.forecast.ApiTokenProvider
import app.sigot.data.AppApiUrlProvider
import app.sigot.data.forecast.ApiForecastSource
import app.sigot.data.forecast.AppForecastSource
import app.sigot.data.forecast.AppTokenProvider
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
        factoryOf(::AppTokenProvider) bind ApiTokenProvider::class
        factoryOf(::AppApiUrlProvider) bind ApiUrlProvider::class
        factoryOf(::ApiForecastSource) bind ForecastSource::class
        factory {
            val directSource = directApiFortuneSource()
            AppForecastSource(
                settingsRepo = get(),
                backendSource = get<ApiForecastSource>(),
                directSource = directSource,
            )
        } bind ForecastSource::class
    }
