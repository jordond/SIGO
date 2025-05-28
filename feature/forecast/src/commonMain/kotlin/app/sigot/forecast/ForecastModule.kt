package app.sigot.forecast

import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.GetScoreUseCase
import app.sigot.core.domain.forecast.ScoreCalculator
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
import app.sigot.forecast.domain.AppConfigScoreCalculator
import app.sigot.forecast.domain.DefaultGetForecastUseCase
import app.sigot.forecast.domain.DefaultGetScoreUseCast
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
        factoryOf(::DefaultGetScoreUseCast) bind GetScoreUseCase::class

        factoryOf(::DefaultVisualCrossingApi) bind VisualCrossingApi::class
        singleOf(::DefaultForecastRepo) bind ForecastRepo::class
    }

public fun Scope.directApiFortuneSource(): ForecastSource =
    VisualCrossingForecastSource(get(), get(), get(), get())

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
    }

public fun forecastBackendModule(): Module =
    module {
        factory<QueryCostLogger> {
            val logger = get<AnalyticsLogger>()
            // TODO: Have a real solution to logging the cost
            QueryCostLogger { cost ->
                logger.log("Query cost", mapOf("cost" to cost.toString()))
            }
        }

        factoryOf(::VisualCrossingForecastSource) bind ForecastSource::class
    }

public fun forecastCliModule(): Module =
    module {
        includes(forecastBaseModule())
        factory<QueryCostLogger> { QueryCostLogger {} }
        single { StoreForecastCache(store = NoopStore(), get(), get()) } bind ForecastCache::class
        factoryOf(::VisualCrossingForecastSource) bind ForecastSource::class
    }
