package app.sigot.forecast

import app.sigot.core.domain.forecast.ClearForecastUseCase
import app.sigot.core.domain.forecast.DefaultScoreCalculator
import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.domain.forecast.ForecastStateHolder
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.GetScoreUseCase
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.foundation.analytics.AnalyticsLogger
import app.sigot.core.platform.store.Store
import app.sigot.forecast.data.CacheForecastRepo
import app.sigot.forecast.data.DefaultForecastRepo
import app.sigot.forecast.data.source.ForecastCache
import app.sigot.forecast.data.source.ForecastSource
import app.sigot.forecast.data.source.QueryCostLogger
import app.sigot.forecast.data.source.cache.StoreForecastCache
import app.sigot.forecast.data.source.visualcrossing.DefaultVisualCrossingApi
import app.sigot.forecast.data.source.visualcrossing.VisualCrossingApi
import app.sigot.forecast.data.source.visualcrossing.VisualCrossingForecastSource
import app.sigot.forecast.domain.AppConfigScoreCalculator
import app.sigot.forecast.domain.DefaultClearForecastUseCase
import app.sigot.forecast.domain.DefaultForecastStateHolder
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
