package app.sigot.core.api.server.cache

/**
 * Provides access to the KV forecast cache.
 * The cache instance is set once from the Cloudflare env binding on the first request
 * and reused for the lifetime of the Worker isolate (single-threaded).
 */
public class ForecastCacheProvider : CacheProvider {
    override var cache: ApiCache? = null
}
