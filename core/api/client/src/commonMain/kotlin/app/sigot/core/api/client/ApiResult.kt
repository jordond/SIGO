package app.sigot.core.api.client

import app.sigot.core.api.server.http.RateLimit

/**
 * Wraps an API response with optional rate-limit metadata.
 */
public data class ApiResult<out T>(
    val data: T,
    val rateLimit: RateLimit?,
)
