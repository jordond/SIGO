package app.sigot.core.api.server.ratelimit

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.uuid.Uuid

class DefaultRateLimiterAttestedTest {
    private val json = Json
    private val client1 = Uuid.parse("00000000-0000-0000-0000-000000000001")

    @Test
    fun unattestedClientUsesDefaultLimit() =
        runTest {
            val limiter = DefaultRateLimiter(
                json = json,
                maxRequestsPerClient = 5,
                maxRequestsPerClientAttested = 20,
                maxRequestsPerIp = 100,
                maxRequestsPerIpAttested = 400,
            )
            val cache = FakeApiCache()

            val result = limiter.check(client1, "1.2.3.4", cache, attested = false)
            result.allowed shouldBe true
            result.limit shouldBe 5
            result.remaining shouldBe 4
        }

    @Test
    fun attestedClientUsesHigherLimit() =
        runTest {
            val limiter = DefaultRateLimiter(
                json = json,
                maxRequestsPerClient = 5,
                maxRequestsPerClientAttested = 20,
                maxRequestsPerIp = 100,
                maxRequestsPerIpAttested = 400,
            )
            val cache = FakeApiCache()

            val result = limiter.check(client1, "1.2.3.4", cache, attested = true)
            result.allowed shouldBe true
            result.limit shouldBe 20
            result.remaining shouldBe 19
        }

    @Test
    fun unattestedClientBlockedAtDefaultLimit() =
        runTest {
            val limiter = DefaultRateLimiter(
                json = json,
                maxRequestsPerClient = 2,
                maxRequestsPerClientAttested = 10,
                maxRequestsPerIp = 100,
                maxRequestsPerIpAttested = 400,
            )
            val cache = FakeApiCache()

            repeat(2) { limiter.check(client1, "1.2.3.4", cache, attested = false) }
            val result = limiter.check(client1, "1.2.3.4", cache, attested = false)
            result.allowed shouldBe false
        }

    @Test
    fun attestedClientAllowedBeyondDefaultLimit() =
        runTest {
            val limiter = DefaultRateLimiter(
                json = json,
                maxRequestsPerClient = 2,
                maxRequestsPerClientAttested = 10,
                maxRequestsPerIp = 100,
                maxRequestsPerIpAttested = 400,
            )
            val cache = FakeApiCache()

            // Use 2 requests (would be blocked for unattested)
            repeat(2) { limiter.check(client1, "1.2.3.4", cache, attested = true) }
            // 3rd request still allowed for attested
            val result = limiter.check(client1, "1.2.3.4", cache, attested = true)
            result.allowed shouldBe true
            result.remaining shouldBe 7
        }

    @Test
    fun ipLimitUsesAttestedValueWhenAttested() =
        runTest {
            val client2 = Uuid.parse("00000000-0000-0000-0000-000000000002")
            val client3 = Uuid.parse("00000000-0000-0000-0000-000000000003")
            val limiter = DefaultRateLimiter(
                json = json,
                maxRequestsPerClient = 100,
                maxRequestsPerClientAttested = 400,
                maxRequestsPerIp = 2,
                maxRequestsPerIpAttested = 10,
            )
            val cache = FakeApiCache()

            // 2 requests from different clients, same IP (would exhaust unattested IP limit)
            limiter.check(client1, "1.2.3.4", cache, attested = true)
            limiter.check(client2, "1.2.3.4", cache, attested = true)

            // 3rd from same IP, different client - still allowed because attested IP limit is 10
            val result = limiter.check(client3, "1.2.3.4", cache, attested = true)
            result.allowed shouldBe true
        }

    @Test
    fun defaultParameterIsFalse() =
        runTest {
            val limiter = DefaultRateLimiter(
                json = json,
                maxRequestsPerClient = 5,
                maxRequestsPerClientAttested = 20,
                maxRequestsPerIp = 100,
            )
            val cache = FakeApiCache()

            // Call without attested parameter (uses default = false)
            val result = limiter.check(client1, "1.2.3.4", cache)
            result.limit shouldBe 5
        }
}
