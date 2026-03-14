package app.sigot.core.api.server.cors

import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse

/**
 * Handles CORS origin validation, preflight, and response header injection.
 */
public class DefaultCorsHandler(
    private val allowedOrigins: Set<String> = DEFAULT_ORIGINS,
) : CorsHandler {
    override fun validateOrigin(request: ServerRequest): ServerResponse? {
        val origin = request.headers["Origin"] ?: return null
        if (origin !in allowedOrigins) {
            return forbidden()
        }
        return null
    }

    override fun preflight(request: ServerRequest): ServerResponse {
        val origin = request.headers["Origin"]
        if (origin != null && origin !in allowedOrigins) {
            return forbidden()
        }

        val headers = mutableMapOf<String, String>()
        if (origin != null) {
            headers["Access-Control-Allow-Origin"] = origin
        }
        headers["Access-Control-Allow-Methods"] = ALLOWED_METHODS
        headers["Access-Control-Allow-Headers"] = ALLOWED_HEADERS
        headers["Access-Control-Max-Age"] = "86400"
        headers["Vary"] = "Origin"

        return ServerResponse(
            statusCode = 204,
            statusText = "No Content",
            headers = headers,
        )
    }

    override fun withCorsHeaders(
        response: ServerResponse,
        request: ServerRequest,
    ): ServerResponse {
        val origin = request.headers["Origin"]
        if (origin == null || origin !in allowedOrigins) {
            return response
        }

        val headers = response.headers.toMutableMap()
        headers["Access-Control-Allow-Origin"] = origin
        headers["Access-Control-Allow-Methods"] = ALLOWED_METHODS
        headers["Access-Control-Allow-Headers"] = ALLOWED_HEADERS
        headers["Access-Control-Expose-Headers"] =
            "X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset"
        headers["Vary"] = "Origin"

        return response.copy(headers = headers)
    }

    private fun forbidden(): ServerResponse =
        ServerResponse(
            statusCode = 403,
            statusText = "Forbidden",
            headers = mutableMapOf("content-type" to "application/json", "Vary" to "Origin"),
            body = """{"error":"Forbidden: Origin not allowed"}""",
        )

    public companion object {
        private const val ALLOWED_METHODS = "GET, OPTIONS"
        private const val ALLOWED_HEADERS = "Content-Type, X-Client-ID"
        public val DEFAULT_ORIGINS: Set<String> = setOf(
            "https://shouldigooutside.now",
            "https://staging.shouldigooutside.now",
            "http://localhost:4321",
            "http://localhost:3000",
        )
    }
}
