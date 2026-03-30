package now.shouldigooutside.api.provider

import now.shouldigooutside.core.api.server.cache.ApiCache
import now.shouldigooutside.core.api.server.cache.CacheProvider

/**
 * Provides access to the KV forecast cache.
 * The cache instance is set once from the Cloudflare env binding on the first request
 * and reused for the lifetime of the Worker isolate (single-threaded).
 */
class KvCacheProvider : CacheProvider {
    override var cache: ApiCache? = null
}
