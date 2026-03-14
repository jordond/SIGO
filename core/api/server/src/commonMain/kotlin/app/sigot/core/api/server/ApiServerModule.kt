package app.sigot.core.api.server

import app.sigot.core.api.server.cache.CacheProvider
import app.sigot.core.api.server.cors.CorsHandler
import app.sigot.core.api.server.cors.DefaultCorsHandler
import app.sigot.core.api.server.ratelimit.DefaultRateLimiter
import app.sigot.core.api.server.ratelimit.RateLimiter
import app.sigot.core.api.server.routes.VersionRoute
import app.sigot.core.api.server.routes.forecast.ForecastRoute
import app.sigot.core.api.server.routes.forecast.score.ForecastScoreRoute
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun commonApiServerModule(): Module =
    module {
        single<RateLimiter> { DefaultRateLimiter(json = get()) }
        single<CorsHandler> { DefaultCorsHandler() }

        factoryOf(::VersionRoute) bind ApiRoute::class
        factoryOf(::ForecastRoute) bind ApiRoute::class
        factoryOf(::ForecastScoreRoute) bind ApiRoute::class

        single<ApiRouter> {
            DefaultApiRouter(
                routes = getAll(),
                json = get(),
                cacheProvider = get(),
                rateLimiter = get(),
                corsHandler = get(),
            )
        }
    }
