package app.sigot.core.api.server.ratelimit

import app.sigot.core.api.server.cache.ApiCache
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
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
 *
 * Note: KV is eventually consistent and does not support atomic read-modify-write,
 * so enforcement is approximate under concurrent requests from the same client.
 * Note: On JVM with InMemoryApiCache, the read-modify-write is also non-atomic under
 * concurrent requests. This is acceptable for rate limiting where approximate enforcement
 * is sufficient.
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
    ): RateLimiter.RateLimitResult {
        val nowSeconds = clock.now().epochSeconds
        val clientResult = checkKey("ratelimit:$clientId", maxRequestsPerClient, nowSeconds, cache)
        val ipResult = if (ipAddress != null) {
            checkKey("ratelimit:ip:$ipAddress", maxRequestsPerIp, nowSeconds, cache)
        } else {
            null
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
        cache.put(key, json.encodeToString(updated), ttl)

        return RateLimiter.RateLimitResult(
            allowed = allowed,
            limit = maxRequests,
            remaining = remaining,
            resetAt = Instant.fromEpochSeconds(resetEpoch),
        )
    }
}
