package app.sigot.core.api.server.cache

import kotlinx.coroutines.await
import kotlin.time.Duration

/**
 * Thin wrapper around a Cloudflare KV namespace for caching forecast responses.
 */
public class KvForecastCache(
    private val kv: dynamic,
) : ApiCache {
    /**
     * Get a cached value by key. Returns null on cache miss.
     */
    override suspend fun get(key: String): String? {
        val promise = kv.get(key) as kotlin.js.Promise<String?>
        return promise.await()
    }

    /**
     * Put a value into the cache with a TTL in seconds.
     */
    override suspend fun put(
        key: String,
        value: String,
        ttl: Duration,
    ) {
        val options = js("({})")
        options.expirationTtl = ttl.inWholeSeconds
        @Suppress("UNCHECKED_CAST")
        val promise = kv.put(key, value, options) as kotlin.js.Promise<dynamic>
        promise.await()
    }
}
