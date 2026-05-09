package now.shouldigooutside.core.api.server

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.api.server.cache.ApiCache
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private class RecordingApiCache : ApiCache {
    val puts = mutableListOf<Triple<String, String, Duration>>()

    override suspend fun get(key: String): String? = null

    override suspend fun put(
        key: String,
        value: String,
        ttl: Duration,
    ) {
        puts += Triple(key, value, ttl)
    }
}

private class CapturingExecutionContext : ExecutionContext {
    val pending = mutableListOf<suspend () -> Unit>()

    override fun waitUntil(block: suspend () -> Unit) {
        pending += block
    }

    suspend fun drain() {
        val snapshot = pending.toList()
        pending.clear()
        snapshot.forEach { it() }
    }
}

class ExecutionContextTest {
    @Test
    fun putDeferredNoOpsWhenCacheIsNull() =
        runTest {
            val context = CapturingExecutionContext()

            context.putDeferred(cache = null, key = "k", value = "v", ttl = 60.seconds)

            context.pending.size shouldBe 0
        }

    @Test
    fun putDeferredSchedulesPutWhenCachePresent() =
        runTest {
            val context = CapturingExecutionContext()
            val cache = RecordingApiCache()

            context.putDeferred(cache = cache, key = "k", value = "v", ttl = 60.seconds)

            cache.puts.size shouldBe 0
            context.pending.size shouldBe 1

            context.drain()

            cache.puts shouldBe listOf(Triple("k", "v", 60.seconds))
        }
}
