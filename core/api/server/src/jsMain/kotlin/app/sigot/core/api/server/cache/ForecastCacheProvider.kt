package app.sigot.core.api.server.cache

import kotlin.time.Duration.Companion.seconds

public const val FORECAST_CACHE_TTL_SECONDS: Int = 900
public val FORECAST_CACHE_TTL_DURATION: kotlin.time.Duration = FORECAST_CACHE_TTL_SECONDS.seconds

/**
 * Provides access to the KV forecast cache.
 * The cache instance is set once from the Cloudflare env binding on the first request
 * and reused for the lifetime of the Worker isolate (single-threaded).
 */
public class ForecastCacheProvider {
    public var cache: KvForecastCache? = null
}
