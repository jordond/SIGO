package now.shouldigooutside.core.api.server.ratelimit

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.server.ExecutionContext
import now.shouldigooutside.core.api.server.cache.ApiCache
import now.shouldigooutside.core.api.server.putDeferred
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
internal data class RateLimitEntry(
    val count: Int,
    val windowStart: Long,
)

/**
 * Fixed-window rate limiter backed by [ApiCache].
 *
 * Enforces two independent limits:
 *  - Per client ID: [maxRequestsPerClient] per [window]
 *  - Per IP address: [maxRequestsPerIp] per [window]
 *
 * The IP limit prevents abuse via client-ID rotation.
 */
public class DefaultRateLimiter(
    private val json: Json,
    private val clock: Clock = Clock.System,
    private val maxRequestsPerClient: Int = 30,
    private val maxRequestsPerIp: Int = 60,
    private val window: Duration = 1.hours,
) : RateLimiter {
    override suspend fun check(
        clientId: Uuid,
        ipAddress: String?,
        cache: ApiCache,
        executionContext: ExecutionContext,
    ): RateLimiter.RateLimitResult {
        val nowSeconds = clock.now().epochSeconds
        val (clientResult, ipResult) = coroutineScope {
            val client = async {
                checkKey(
                    key = "ratelimit:$clientId",
                    maxRequests = maxRequestsPerClient,
                    nowSeconds = nowSeconds,
                    cache = cache,
                    executionContext = executionContext,
                )
            }
            val ip = ipAddress?.let {
                async {
                    checkKey(
                        key = "ratelimit:ip:$it",
                        maxRequests = maxRequestsPerIp,
                        nowSeconds = nowSeconds,
                        cache = cache,
                        executionContext = executionContext,
                    )
                }
            }
            client.await() to ip?.await()
        }

        val ipBlocked = ipResult != null && !ipResult.allowed
        return if (!clientResult.allowed || ipBlocked) {
            val blocking = if (!clientResult.allowed) clientResult else ipResult!!
            blocking.copy(allowed = false)
        } else {
            clientResult
        }
    }

    private suspend fun checkKey(
        key: String,
        maxRequests: Int,
        nowSeconds: Long,
        cache: ApiCache,
        executionContext: ExecutionContext,
    ): RateLimiter.RateLimitResult {
        val existing = cache.get(key)
        val entry = if (existing != null) {
            try {
                json.decodeFromString<RateLimitEntry>(existing)
            } catch (_: SerializationException) {
                null
            }
        } else {
            null
        }

        val windowSeconds = window.inWholeSeconds
        val currentWindow = if (entry != null && (nowSeconds - entry.windowStart) < windowSeconds) {
            entry
        } else {
            RateLimitEntry(count = 0, windowStart = nowSeconds)
        }

        val newCount = currentWindow.count + 1
        val resetEpoch = currentWindow.windowStart + windowSeconds
        val allowed = newCount <= maxRequests
        val remaining = (maxRequests - newCount).coerceAtLeast(0)

        val updated = currentWindow.copy(count = newCount)
        val ttl = (resetEpoch - nowSeconds).coerceAtLeast(1).seconds
        executionContext.putDeferred(cache, key, json.encodeToString(updated), ttl)

        return RateLimiter.RateLimitResult(
            allowed = allowed,
            limit = maxRequests,
            remaining = remaining,
            resetAt = Instant.fromEpochSeconds(resetEpoch),
        )
    }
}
