package app.sigot.core.api.server.http

/**
 * Type-safe HTTP header name constants shared between server and client.
 */
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

    public const val CONTENT_TYPE: String = "Content-Type"
    public const val CACHE_CONTROL: String = "Cache-Control"
    public const val ORIGIN: String = "Origin"
    public const val VARY: String = "Vary"

    // CORS headers
    public const val ACCESS_CONTROL_ALLOW_ORIGIN: String = "Access-Control-Allow-Origin"
    public const val ACCESS_CONTROL_ALLOW_METHODS: String = "Access-Control-Allow-Methods"
    public const val ACCESS_CONTROL_ALLOW_HEADERS: String = "Access-Control-Allow-Headers"
    public const val ACCESS_CONTROL_MAX_AGE: String = "Access-Control-Max-Age"
    public const val ACCESS_CONTROL_EXPOSE_HEADERS: String = "Access-Control-Expose-Headers"
}
