package app.sigot.core.api.server.cache

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration

/**
 * In-memory [ApiCache] implementation backed by [ConcurrentHashMap] with TTL-based expiry.
 *
 * Entries are tracked with their expiration time. A background coroutine loop runs on
 * [sweepInterval], removing any expired entries.
 */
public class InMemoryApiCache(
    scope: CoroutineScope,
    private val sweepInterval: Duration = FORECAST_CACHE_TTL,
) : ApiCache {
    private data class Entry(
        val value: String,
        val expiresAtMillis: Long,
    )

    private val store = ConcurrentHashMap<String, Entry>()

    init {
        scope.launch {
            while (isActive) {
                delay(sweepInterval)
                sweep()
            }
        }
    }

    override suspend fun get(key: String): String? {
        var result: String? = null
        store.computeIfPresent(key) { _, entry ->
            if (System.currentTimeMillis() < entry.expiresAtMillis) {
                result = entry.value
                entry // keep
            } else {
                null // remove expired
            }
        }
        return result
    }

    override suspend fun put(
        key: String,
        value: String,
        ttl: Duration,
    ) {
        val expiresAtMillis = System.currentTimeMillis() + ttl.inWholeMilliseconds
        store.compute(key) { _, _ -> Entry(value, expiresAtMillis) }
    }

    private fun sweep() {
        val now = System.currentTimeMillis()
        store.entries.removeIf { (_, entry) -> now >= entry.expiresAtMillis }
    }
}
