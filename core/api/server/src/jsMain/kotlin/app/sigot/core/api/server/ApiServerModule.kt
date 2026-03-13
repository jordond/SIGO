package app.sigot.core.api.server

import app.sigot.core.api.server.cache.ForecastCacheProvider
import app.sigot.core.api.server.ratelimit.RateLimiter
import app.sigot.core.api.server.routes.VersionRoute
import app.sigot.core.api.server.routes.forecast.ForecastRoute
import app.sigot.core.api.server.routes.forecast.score.ForecastScoreRoute
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun jsApiServerModule(): Module =
    module {
        single { ForecastCacheProvider() }
        single { RateLimiter(json = get()) }

        factoryOf(::VersionRoute) bind ApiRoute::class
        factoryOf(::ForecastRoute) bind ApiRoute::class
        factoryOf(::ForecastScoreRoute) bind ApiRoute::class

        single {
            DefaultApiRouter(
                routes = getAll(),
                json = get(),
                cacheProvider = get(),
                rateLimiter = get(),
            )
        } bind ApiRouter::class
    }
