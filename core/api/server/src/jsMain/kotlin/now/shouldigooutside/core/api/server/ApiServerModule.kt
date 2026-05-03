package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.routes.VersionRoute
import now.shouldigooutside.core.api.server.routes.forecast.ForecastRoute
import now.shouldigooutside.core.api.server.routes.forecast.score.ForecastScoreRoute
import org.koin.core.module.Module
import org.koin.dsl.ScopeDSL
import org.koin.dsl.bind
import org.koin.dsl.module

public fun jsApiServerModule(): Module =
    module {
        includes(commonApiServerModule())
    }

/**
 * Registers per-request scoped API router definitions into the calling [ScopeDSL] block.
 * Intended for use inside `scope<RequestScope> { scopedApiRouterDefinitions() }` in
 * platform-specific DI modules that cannot access the `internal` [DefaultApiRouter] directly.
 */
public fun ScopeDSL.scopedApiRouterDefinitions() {
    scoped { VersionRoute(get()) } bind ApiRoute::class
    scoped { ForecastRoute(get(), get(), get()) } bind ApiRoute::class
    scoped { ForecastScoreRoute(get(), get(), get(), get()) } bind ApiRoute::class

    scoped<ApiRouter> {
        DefaultApiRouter(
            routes = getAll<ApiRoute>(),
            json = get(),
            cacheProvider = get(),
            rateLimiter = get(),
            corsHandler = get(),
        )
    }
}
