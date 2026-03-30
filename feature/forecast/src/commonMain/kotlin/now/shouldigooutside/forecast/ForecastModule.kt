package now.shouldigooutside.forecast

import now.shouldigooutside.core.domain.forecast.ClearForecastUseCase
import now.shouldigooutside.core.domain.forecast.DefaultScoreCalculator
import now.shouldigooutside.core.domain.forecast.ForecastRepo
import now.shouldigooutside.core.domain.forecast.ForecastStateHolder
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.GetScoreUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.foundation.analytics.AnalyticsLogger
import now.shouldigooutside.core.platform.store.Store
import now.shouldigooutside.forecast.data.CacheForecastRepo
import now.shouldigooutside.forecast.data.DefaultForecastRepo
import now.shouldigooutside.forecast.data.source.ForecastCache
import now.shouldigooutside.forecast.data.source.ForecastSource
import now.shouldigooutside.forecast.data.source.QueryCostLogger
import now.shouldigooutside.forecast.data.source.cache.StoreForecastCache
import now.shouldigooutside.forecast.data.source.visualcrossing.DefaultVisualCrossingApi
import now.shouldigooutside.forecast.data.source.visualcrossing.VisualCrossingApi
import now.shouldigooutside.forecast.data.source.visualcrossing.VisualCrossingForecastSource
import now.shouldigooutside.forecast.domain.AppConfigScoreCalculator
import now.shouldigooutside.forecast.domain.DefaultClearForecastUseCase
import now.shouldigooutside.forecast.domain.DefaultForecastStateHolder
import now.shouldigooutside.forecast.domain.DefaultGetForecastUseCase
import now.shouldigooutside.forecast.domain.DefaultGetScoreUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

private fun forecastBaseModule(): Module =
    module {
        factoryOf(::AppConfigScoreCalculator) bind ScoreCalculator::class
        factoryOf(::DefaultGetForecastUseCase) bind GetForecastUseCase::class
        factoryOf(::DefaultGetScoreUseCase) bind GetScoreUseCase::class

        factoryOf(::DefaultVisualCrossingApi) bind VisualCrossingApi::class

        factoryOf(::DefaultClearForecastUseCase) bind ClearForecastUseCase::class
    }

public fun Scope.directApiFortuneSource(): ForecastSource = VisualCrossingForecastSource(get(), get(), get())

public fun forecastAppModule(): Module =
    module {
        includes(forecastBaseModule())

        single {
            StoreForecastCache(
                store = Store.storeOf("forecast_cache.json", Store.Type.Cache),
                appConfigRepo = get(),
                nowProvider = get(),
            )
        } bind ForecastCache::class

        factory {
            CacheForecastRepo(
                cache = get(),
                nowProvider = get(),
                delegate = DefaultForecastRepo(get(), get()),
            )
        } bind ForecastRepo::class

        singleOf(::DefaultForecastStateHolder) bind ForecastStateHolder::class
    }

public fun forecastBackendModule(): Module =
    module {
        factory<QueryCostLogger> {
            val logger = get<AnalyticsLogger>()
            QueryCostLogger { cost ->
                logger.log("Query cost", mapOf("cost" to cost.toString()))
            }
        }

        factory { DefaultScoreCalculator() } bind ScoreCalculator::class
        factoryOf(::DefaultVisualCrossingApi) bind VisualCrossingApi::class
        factoryOf(::VisualCrossingForecastSource) bind ForecastSource::class
        factory { DefaultForecastRepo(get(), null) } bind ForecastRepo::class
        factoryOf(::DefaultGetForecastUseCase) bind GetForecastUseCase::class
    }

public fun forecastCliModule(): Module =
    module {
        includes(forecastBaseModule())
        factory<QueryCostLogger> { QueryCostLogger {} }
        factoryOf(::VisualCrossingForecastSource) bind ForecastSource::class
        factory { DefaultForecastRepo(get(), null) } bind ForecastRepo::class
    }
