package app.sigot.core.api.model.http

public object ApiHeaders {
    /** Client-generated UUID sent on every request for identification and rate limiting. */
    public const val CLIENT_ID: String = "X-Client-ID"

    /** Cloudflare-injected real client IP address. */
    public const val CONNECTING_IP: String = "CF-Connecting-IP"

    /** Maximum number of requests allowed in the current rate-limit window. */
    public const val RATE_LIMIT: String = "X-RateLimit-Limit"

    /** Number of requests remaining in the current rate-limit window. */
    public const val RATE_LIMIT_REMAINING: String = "X-RateLimit-Remaining"

    /** Epoch seconds when the current rate-limit window resets. */
    public const val RATE_LIMIT_RESET: String = "X-RateLimit-Reset"
}
