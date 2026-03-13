package app.sigot.core.api.server.cache

/**
 * Provides access to the KV forecast cache.
 * The cache instance is set at request time from the Cloudflare env binding.
 */
public class ForecastCacheProvider {
    public var cache: KvForecastCache? = null
}
