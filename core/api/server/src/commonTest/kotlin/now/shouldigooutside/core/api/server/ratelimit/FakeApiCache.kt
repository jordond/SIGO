package now.shouldigooutside.core.api.server.ratelimit

import now.shouldigooutside.core.api.server.cache.ApiCache
import kotlin.time.Duration

internal class FakeApiCache : ApiCache {
    private val store = mutableMapOf<String, String>()

    override suspend fun get(key: String): String? = store[key]

    override suspend fun put(
        key: String,
        value: String,
        ttl: Duration,
    ) {
        store[key] = value
    }
}
