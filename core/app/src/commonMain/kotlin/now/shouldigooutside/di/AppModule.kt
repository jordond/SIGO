package now.shouldigooutside.di

import now.shouldigooutside.core.api.client.ApiUrlProvider
import now.shouldigooutside.core.domain.forecast.ApiTokenProvider
import now.shouldigooutside.data.AppApiUrlProvider
import now.shouldigooutside.data.forecast.ApiForecastSource
import now.shouldigooutside.data.forecast.AppForecastSource
import now.shouldigooutside.data.forecast.AppTokenProvider
import now.shouldigooutside.forecast.data.source.ForecastSource
import now.shouldigooutside.forecast.data.source.QueryCostLogger
import now.shouldigooutside.forecast.directApiFortuneSource
import now.shouldigooutside.ui.uiModule
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
