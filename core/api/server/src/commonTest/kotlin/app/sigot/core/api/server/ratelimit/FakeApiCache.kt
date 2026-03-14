package app.sigot.core.api.server.ratelimit

import app.sigot.core.api.server.cache.ApiCache
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
