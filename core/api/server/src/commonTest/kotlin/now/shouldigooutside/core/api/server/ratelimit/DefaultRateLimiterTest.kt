package now.shouldigooutside.core.api.server.ratelimit

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.server.ExecutionContext
import now.shouldigooutside.core.api.server.cache.ApiCache
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

private class CapturingExecutionContext : ExecutionContext {
    private val pending = mutableListOf<suspend () -> Unit>()

    override fun waitUntil(block: suspend () -> Unit) {
        pending += block
    }

    suspend fun drain() {
        val snapshot = pending.toList()
        pending.clear()
        snapshot.forEach { it() }
    }
}

class DefaultRateLimiterTest {
    private val json = Json
    private val executionContext = CapturingExecutionContext()

    private suspend fun RateLimiter.checkAndDrain(
        clientId: Uuid,
        ipAddress: String?,
        cache: ApiCache,
    ): RateLimiter.RateLimitResult {
        val result = check(clientId, ipAddress, cache, executionContext)
        executionContext.drain()
        return result
    }

    private val client1 = Uuid.parse("00000000-0000-0000-0000-000000000001")
    private val client2 = Uuid.parse("00000000-0000-0000-0000-000000000002")
    private val client3 = Uuid.parse("00000000-0000-0000-0000-000000000003")

    private fun fixedClock(epochSeconds: Long): Clock =
        object : Clock {
            override fun now(): Instant = Instant.fromEpochSeconds(epochSeconds)
        }

    @Test
    fun firstRequestIsAllowed() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 5, maxRequestsPerIp = 10)
            val cache = FakeApiCache()

            val result = limiter.checkAndDrain(client1, "1.2.3.4", cache)

            result.allowed shouldBe true
            result.remaining shouldBe 4
            result.limit shouldBe 5
        }

    @Test
    fun requestsUpToLimitAreAllowed() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 3, maxRequestsPerIp = 100)
            val cache = FakeApiCache()

            repeat(3) {
                val result = limiter.checkAndDrain(client1, "1.2.3.4", cache)
                result.allowed shouldBe true
            }
        }

    @Test
    fun clientLimitExceededReturnsDenied() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 2, maxRequestsPerIp = 100)
            val cache = FakeApiCache()

            limiter.checkAndDrain(client1, "1.2.3.4", cache)
            limiter.checkAndDrain(client1, "1.2.3.4", cache)

            val result = limiter.checkAndDrain(client1, "1.2.3.4", cache)
            result.allowed shouldBe false
            result.remaining shouldBe 0
        }

    @Test
    fun ipLimitExceededReturnsDenied() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 100, maxRequestsPerIp = 2)
            val cache = FakeApiCache()

            limiter.checkAndDrain(client1, "1.2.3.4", cache)
            limiter.checkAndDrain(client2, "1.2.3.4", cache)

            val result = limiter.checkAndDrain(client3, "1.2.3.4", cache)
            result.allowed shouldBe false
        }

    @Test
    fun differentClientsHaveIndependentLimits() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 1, maxRequestsPerIp = 100)
            val cache = FakeApiCache()

            val result1 = limiter.checkAndDrain(client1, "1.2.3.4", cache)
            result1.allowed shouldBe true

            val result2 = limiter.checkAndDrain(client2, "5.6.7.8", cache)
            result2.allowed shouldBe true
        }

    @Test
    fun differentIpsHaveIndependentLimits() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 100, maxRequestsPerIp = 1)
            val cache = FakeApiCache()

            val result1 = limiter.checkAndDrain(client1, "1.2.3.4", cache)
            result1.allowed shouldBe true

            val result2 = limiter.checkAndDrain(client1, "5.6.7.8", cache)
            result2.allowed shouldBe true
        }

    @Test
    fun nullIpAddressSkipsIpLimiting() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 100, maxRequestsPerIp = 1)
            val cache = FakeApiCache()

            repeat(5) {
                val result = limiter.checkAndDrain(client1, null, cache)
                result.allowed shouldBe true
            }
        }

    @Test
    fun windowResetsAfterExpiry() =
        runTest {
            val clock1 = fixedClock(1000L)
            val limiter1 = DefaultRateLimiter(
                json = json,
                clock = clock1,
                maxRequestsPerClient = 1,
                maxRequestsPerIp = 100,
                window = 60.seconds,
            )
            val cache = FakeApiCache()

            limiter1.checkAndDrain(client1, "1.2.3.4", cache)
            val denied = limiter1.checkAndDrain(client1, "1.2.3.4", cache)
            denied.allowed shouldBe false

            val clock2 = fixedClock(1061L)
            val limiter2 = DefaultRateLimiter(
                json = json,
                clock = clock2,
                maxRequestsPerClient = 1,
                maxRequestsPerIp = 100,
                window = 60.seconds,
            )

            val afterWindow = limiter2.checkAndDrain(client1, "1.2.3.4", cache)
            afterWindow.allowed shouldBe true
        }

    @Test
    fun resetEpochIsCorrect() =
        runTest {
            val clock = fixedClock(5000L)
            val limiter = DefaultRateLimiter(
                json = json,
                clock = clock,
                maxRequestsPerClient = 10,
                maxRequestsPerIp = 100,
                window = 1.hours,
            )
            val cache = FakeApiCache()

            val result = limiter.checkAndDrain(client1, "1.2.3.4", cache)
            result.resetAt shouldBe Instant.fromEpochSeconds(5000L + 3600)
        }

    @Test
    fun remainingDecrementsCorrectly() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 3, maxRequestsPerIp = 100)
            val cache = FakeApiCache()

            limiter.checkAndDrain(client1, "1.2.3.4", cache).remaining shouldBe 2
            limiter.checkAndDrain(client1, "1.2.3.4", cache).remaining shouldBe 1
            limiter.checkAndDrain(client1, "1.2.3.4", cache).remaining shouldBe 0
            limiter.checkAndDrain(client1, "1.2.3.4", cache).remaining shouldBe 0
        }

    @Test
    fun corruptedCacheEntryIsHandledGracefully() =
        runTest {
            val cache = FakeApiCache()

            cache.put("ratelimit:client-1", "not-valid-json", 1.hours)

            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 5, maxRequestsPerIp = 100)
            val result = limiter.checkAndDrain(client1, null, cache)

            result.allowed shouldBe true
            result.remaining shouldBe 4
        }
}
