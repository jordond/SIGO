package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.cache.CacheProvider
import now.shouldigooutside.core.api.server.cache.ForecastCacheProvider
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public fun jsApiServerModule(): Module =
    module {
        includes(commonApiServerModule())
        single { ForecastCacheProvider() } bind CacheProvider::class
    }
