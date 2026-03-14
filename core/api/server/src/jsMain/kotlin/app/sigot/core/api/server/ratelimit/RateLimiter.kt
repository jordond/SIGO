package app.sigot.core.api.server.ratelimit

import app.sigot.core.api.server.cache.KvForecastCache

public interface RateLimiter {
    public data class RateLimitResult(
        val allowed: Boolean,
        val limit: Int,
        val remaining: Int,
        val resetEpochSeconds: Long,
    )

    /**
     * Check and increment rate limit for the given client ID and IP address.
     * Both limits must pass for the request to be allowed.
     */
    public suspend fun check(
        clientId: String,
        ipAddress: String?,
        cache: KvForecastCache,
    ): RateLimitResult
}
