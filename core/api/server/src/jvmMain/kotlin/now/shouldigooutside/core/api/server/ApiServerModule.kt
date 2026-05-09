@file:JvmName("JvmApiServerModule")

package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.cache.CacheProvider
import now.shouldigooutside.core.api.server.cache.InMemoryApiCache
import now.shouldigooutside.core.api.server.cache.JvmCacheProvider
import now.shouldigooutside.core.api.server.routes.VersionRoute
import now.shouldigooutside.core.api.server.routes.forecast.ForecastRoute
import now.shouldigooutside.core.api.server.routes.forecast.score.ForecastScoreRoute
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun jvmApiServerModule(): Module =
    module {
        includes(commonApiServerModule())

        single<CacheProvider> { JvmCacheProvider(cache = InMemoryApiCache(scope = get())) }
        single<ExecutionContext> { ImmediateExecutionContext(scope = get()) }

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
                executionContext = get(),
            )
        }
    }
