package now.shouldigooutside.core.api.server.ratelimit

import now.shouldigooutside.core.api.server.cache.ApiCache
import kotlin.time.Instant
import kotlin.uuid.Uuid

public interface RateLimiter {
    public data class RateLimitResult(
        val allowed: Boolean,
        val limit: Int,
        val remaining: Int,
        val resetAt: Instant,
    )

    public suspend fun check(
        clientId: Uuid,
        ipAddress: String?,
        cache: ApiCache,
    ): RateLimitResult
}
