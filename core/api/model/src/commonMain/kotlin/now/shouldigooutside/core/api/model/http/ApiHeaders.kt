package now.shouldigooutside.core.api.model.http

public object ApiHeaders {
    /** Client-generated UUID sent on every request for identification. */
    public const val CLIENT_ID: String = "X-Client-ID"

    /** Cloudflare-injected real client IP address. */
    public const val CONNECTING_IP: String = "CF-Connecting-IP"
}
