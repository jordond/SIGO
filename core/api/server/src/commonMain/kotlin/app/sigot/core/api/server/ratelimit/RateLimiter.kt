package app.sigot.core.api.server.ratelimit

import app.sigot.core.api.server.cache.ApiCache
import kotlin.uuid.Uuid

public interface RateLimiter {
    public data class RateLimitResult(
        val allowed: Boolean,
        val limit: Int,
        val remaining: Int,
        val resetEpochSeconds: Long,
    )

    public suspend fun check(
        clientId: Uuid,
        ipAddress: String?,
        cache: ApiCache,
    ): RateLimitResult
}
