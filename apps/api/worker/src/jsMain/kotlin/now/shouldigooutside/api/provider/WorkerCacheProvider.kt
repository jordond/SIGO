package now.shouldigooutside.api.provider

import now.shouldigooutside.core.api.server.cache.ApiCache
import now.shouldigooutside.core.api.server.cache.CacheProvider

/**
 * Immutable [CacheProvider] declared into the Koin RequestScope each fetch.
 * `cache` is null when the worker has no `FORECAST_CACHE` KV binding (e.g.
 * tests or a misconfigured env).
 */
internal class WorkerCacheProvider(
    override val cache: ApiCache?,
) : CacheProvider
