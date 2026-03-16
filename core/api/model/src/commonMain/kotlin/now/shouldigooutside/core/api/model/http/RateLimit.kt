package now.shouldigooutside.core.api.model.http

import kotlin.time.Instant

/**
 * Rate limit state parsed from response headers.
 *
 * @property limit Maximum requests allowed in the current window.
 * @property remaining Requests remaining in the current window.
 * @property resetAt When the current rate-limit window resets.
 */
public data class RateLimit(
    val limit: Int,
    val remaining: Int,
    val resetAt: Instant,
)
