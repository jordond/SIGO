package app.sigot.core.api.server.ratelimit

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Instant

class DefaultRateLimiterTest {
    private val json = Json

    private fun fixedClock(epochSeconds: Long): Clock =
        object : Clock {
            override fun now(): Instant = Instant.fromEpochSeconds(epochSeconds)
        }

    @Test
    fun firstRequestIsAllowed() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 5, maxRequestsPerIp = 10)
            val cache = fakeKvCache()

            val result = limiter.check("client-1", "1.2.3.4", cache)

            result.allowed shouldBe true
            result.remaining shouldBe 4
            result.limit shouldBe 5
        }

    @Test
    fun requestsUpToLimitAreAllowed() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 3, maxRequestsPerIp = 100)
            val cache = fakeKvCache()

            repeat(3) {
                val result = limiter.check("client-1", "1.2.3.4", cache)
                result.allowed shouldBe true
            }
        }

    @Test
    fun clientLimitExceededReturnsDenied() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 2, maxRequestsPerIp = 100)
            val cache = fakeKvCache()

            limiter.check("client-1", "1.2.3.4", cache)
            limiter.check("client-1", "1.2.3.4", cache)

            val result = limiter.check("client-1", "1.2.3.4", cache)
            result.allowed shouldBe false
            result.remaining shouldBe 0
        }

    @Test
    fun ipLimitExceededReturnsDenied() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 100, maxRequestsPerIp = 2)
            val cache = fakeKvCache()

            // Different client IDs but same IP
            limiter.check("client-1", "1.2.3.4", cache)
            limiter.check("client-2", "1.2.3.4", cache)

            val result = limiter.check("client-3", "1.2.3.4", cache)
            result.allowed shouldBe false
        }

    @Test
    fun differentClientsHaveIndependentLimits() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 1, maxRequestsPerIp = 100)
            val cache = fakeKvCache()

            val result1 = limiter.check("client-1", "1.2.3.4", cache)
            result1.allowed shouldBe true

            val result2 = limiter.check("client-2", "5.6.7.8", cache)
            result2.allowed shouldBe true
        }

    @Test
    fun differentIpsHaveIndependentLimits() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 100, maxRequestsPerIp = 1)
            val cache = fakeKvCache()

            val result1 = limiter.check("client-1", "1.2.3.4", cache)
            result1.allowed shouldBe true

            val result2 = limiter.check("client-1", "5.6.7.8", cache)
            result2.allowed shouldBe true
        }

    @Test
    fun nullIpAddressSkipsIpLimiting() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 100, maxRequestsPerIp = 1)
            val cache = fakeKvCache()

            // With null IP, only client limit applies
            repeat(5) {
                val result = limiter.check("client-1", null, cache)
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
                windowSeconds = 60,
            )
            val cache = fakeKvCache()

            limiter1.check("client-1", "1.2.3.4", cache)
            val denied = limiter1.check("client-1", "1.2.3.4", cache)
            denied.allowed shouldBe false

            // Advance past the window
            val clock2 = fixedClock(1061L)
            val limiter2 = DefaultRateLimiter(
                json = json,
                clock = clock2,
                maxRequestsPerClient = 1,
                maxRequestsPerIp = 100,
                windowSeconds = 60,
            )

            val afterWindow = limiter2.check("client-1", "1.2.3.4", cache)
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
                windowSeconds = 3600,
            )
            val cache = fakeKvCache()

            val result = limiter.check("client-1", "1.2.3.4", cache)
            result.resetEpochSeconds shouldBe 5000L + 3600
        }

    @Test
    fun remainingDecrementsCorrectly() =
        runTest {
            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 3, maxRequestsPerIp = 100)
            val cache = fakeKvCache()

            limiter.check("client-1", "1.2.3.4", cache).remaining shouldBe 2
            limiter.check("client-1", "1.2.3.4", cache).remaining shouldBe 1
            limiter.check("client-1", "1.2.3.4", cache).remaining shouldBe 0
            limiter.check("client-1", "1.2.3.4", cache).remaining shouldBe 0 // stays at 0
        }

    @Test
    fun corruptedCacheEntryIsHandledGracefully() =
        runTest {
            val cache = fakeKvCache()

            // Pre-populate cache with invalid JSON
            cache.put("ratelimit:client-1", "not-valid-json", 3600)

            val limiter = DefaultRateLimiter(json = json, maxRequestsPerClient = 5, maxRequestsPerIp = 100)
            val result = limiter.check("client-1", null, cache)

            // Should start a fresh window instead of crashing
            result.allowed shouldBe true
            result.remaining shouldBe 4
        }
}
