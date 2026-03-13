package app.sigot.core.api.server.cors

import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit

/**
 * Handles CORS origin validation, preflight, and response header injection.
 */
public object CorsHandler {
    private val allowedOrigins = setOf(
        "https://shouldigooutside.now",
        "https://staging.shouldigooutside.now",
        "http://localhost:4321",
        "http://localhost:3000",
    )

    private const val ALLOWED_METHODS = "GET, OPTIONS"
    private const val ALLOWED_HEADERS = "Content-Type, X-Client-ID"

    /**
     * Validate the Origin header. Returns a 403 Response if the origin is present but disallowed,
     * or null if the request should proceed.
     */
    public fun validateOrigin(request: Request): Response? {
        val origin = request.headers.get("Origin") ?: return null // absent origin = allow
        if (origin !in allowedOrigins) {
            return forbidden()
        }
        return null
    }

    /**
     * Handle OPTIONS preflight request. Returns a 204 with CORS headers.
     * If origin is present but not allowed, returns 403. If origin is absent,
     * returns a minimal 204 with no CORS origin headers.
     */
    public fun preflight(request: Request): Response {
        val origin = request.headers.get("Origin")
        if (origin != null && origin !in allowedOrigins) {
            return forbidden()
        }

        val headers: dynamic = object {}
        if (origin != null) {
            headers["Access-Control-Allow-Origin"] = origin
        }
        headers["Access-Control-Allow-Methods"] = ALLOWED_METHODS
        headers["Access-Control-Allow-Headers"] = ALLOWED_HEADERS
        headers["Access-Control-Max-Age"] = "86400"
        headers["Vary"] = "Origin"

        return Response(
            null,
            ResponseInit(status = 204, statusText = "No Content", headers = headers),
        )
    }

    /**
     * Add CORS headers to an existing response. Constructs a new Response from scratch
     * to avoid immutability issues with existing response headers.
     */
    public fun withCorsHeaders(
        response: Response,
        request: Request,
    ): Response {
        val origin = request.headers.get("Origin")
        if (origin == null || origin !in allowedOrigins) {
            return response
        }

        val headers: dynamic = object {}
        response.headers.asDynamic().forEach { value: String, key: String ->
            headers[key] = value
        }
        headers["Access-Control-Allow-Origin"] = origin
        headers["Access-Control-Allow-Methods"] = ALLOWED_METHODS
        headers["Access-Control-Allow-Headers"] = ALLOWED_HEADERS
        headers["Access-Control-Expose-Headers"] =
            "X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset"
        headers["Vary"] = "Origin"

        return Response(
            response.body,
            ResponseInit(
                status = response.status,
                statusText = response.statusText,
                headers = headers,
            ),
        )
    }

    private fun forbidden(): Response {
        val headers: dynamic = object {}
        headers["content-type"] = "application/json"
        return Response(
            """{"error":"Forbidden: Origin not allowed"}""",
            ResponseInit(status = 403, statusText = "Forbidden", headers = headers),
        )
    }
}
