package app.sigot.forecast

import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.foundation.analytics.AnalyticsLogger
import app.sigot.core.platform.store.NoopStore
import app.sigot.core.platform.store.Store
import app.sigot.forecast.data.DefaultForecastRepo
import app.sigot.forecast.data.source.ForecastCache
import app.sigot.forecast.data.source.ForecastSource
import app.sigot.forecast.data.source.QueryCostLogger
import app.sigot.forecast.data.source.cache.StoreForecastCache
import app.sigot.forecast.data.source.visualcrossing.DefaultVisualCrossingApi
import app.sigot.forecast.data.source.visualcrossing.VisualCrossingApi
import app.sigot.forecast.data.source.visualcrossing.VisualCrossingForecastSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun forecastAppModule(): Module =
    module {
        factory<QueryCostLogger> {
            val logger = get<AnalyticsLogger>()
            // TODO: Have a real solution to logging the cost
            QueryCostLogger { cost ->
                logger.log("Query cost", mapOf("cost" to cost.toString()))
            }
        }

        single {
            StoreForecastCache(
                store = Store.storeOf("forecast_cache.json", Store.Type.Cache),
                nowProvider = get(),
            )
        } bind ForecastCache::class

        factoryOf(::DefaultVisualCrossingApi) bind VisualCrossingApi::class
        factoryOf(::VisualCrossingForecastSource) bind ForecastSource::class
        singleOf(::DefaultForecastRepo) bind ForecastRepo::class
    }

public fun forecastCliModule(): Module =
    module {
        factory<QueryCostLogger> { QueryCostLogger {} }
        single { StoreForecastCache(store = NoopStore(), nowProvider = get()) } bind ForecastCache::class
        factoryOf(::DefaultVisualCrossingApi) bind VisualCrossingApi::class
        factoryOf(::VisualCrossingForecastSource) bind ForecastSource::class
        singleOf(::DefaultForecastRepo) bind ForecastRepo::class
    }
