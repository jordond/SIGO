package now.shouldigooutside.api.provider

import kotlinx.coroutines.await
import now.shouldigooutside.core.api.server.cache.ApiCache
import kotlin.js.Promise
import kotlin.time.Duration

/**
 * Per-request wrapper around a Cloudflare KV namespace binding. Constructed
 * from `env.FORECAST_CACHE` inside the Koin RequestScope; never cached
 * across requests.
 */
internal class EnvKvCache(
    private val kv: dynamic,
) : ApiCache {
    override suspend fun get(key: String): String? {
        @Suppress("UNCHECKED_CAST")
        val promise = kv.get(key) as Promise<String?>
        return promise.await()
    }

    override suspend fun put(
        key: String,
        value: String,
        ttl: Duration,
    ) {
        val options = js("({})")
        options.expirationTtl = ttl.inWholeSeconds
        @Suppress("UNCHECKED_CAST")
        val promise = kv.put(key, value, options) as Promise<dynamic>
        promise.await()
    }
}
