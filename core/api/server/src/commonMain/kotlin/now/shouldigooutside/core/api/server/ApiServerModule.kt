package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.cors.CorsHandler
import now.shouldigooutside.core.api.server.cors.DefaultCorsHandler
import now.shouldigooutside.core.api.server.ratelimit.DefaultRateLimiter
import now.shouldigooutside.core.api.server.ratelimit.RateLimiter
import now.shouldigooutside.core.api.server.routes.VersionRoute
import now.shouldigooutside.core.api.server.routes.forecast.ForecastRoute
import now.shouldigooutside.core.api.server.routes.forecast.score.ForecastScoreRoute
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
