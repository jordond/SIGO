@file:JvmName("JvmApiServerModule")

package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.cache.CacheProvider
import now.shouldigooutside.core.api.server.cache.InMemoryApiCache
import now.shouldigooutside.core.api.server.cache.JvmCacheProvider
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public fun jvmApiServerModule(): Module =
    module {
        includes(commonApiServerModule())
        single<CacheProvider> { JvmCacheProvider(cache = InMemoryApiCache(scope = get())) }
    }
