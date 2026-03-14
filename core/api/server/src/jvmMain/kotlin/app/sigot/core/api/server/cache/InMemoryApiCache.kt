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
        val entry = store[key] ?: return null
        return if (System.currentTimeMillis() < entry.expiresAtMillis) {
            entry.value
        } else {
            store.remove(key)
            null
        }
    }

    override suspend fun put(
        key: String,
        value: String,
        ttl: Duration,
    ) {
        val expiresAtMillis = System.currentTimeMillis() + ttl.inWholeMilliseconds
        store[key] = Entry(value, expiresAtMillis)
    }

    private fun sweep() {
        val now = System.currentTimeMillis()
        store.entries.removeIf { (_, entry) -> now >= entry.expiresAtMillis }
    }
}
