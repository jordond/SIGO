package app.sigot.core.api.server.cors

import app.sigot.core.api.server.http.ApiHeaders
import app.sigot.core.api.server.http.ContentType
import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse

/**
 * Handles CORS origin validation, preflight, and response header injection.
 */
public class DefaultCorsHandler(
    private val allowedOrigins: Set<String> = DEFAULT_ORIGINS,
) : CorsHandler {
    override fun validateOrigin(request: ServerRequest): ServerResponse? {
        val origin = request.headers[ApiHeaders.ORIGIN] ?: return null
        if (origin !in allowedOrigins) {
            return forbidden()
        }
        return null
    }

    override fun preflight(request: ServerRequest): ServerResponse {
        val origin = request.headers[ApiHeaders.ORIGIN]
        if (origin != null && origin !in allowedOrigins) {
            return forbidden()
        }

        val headers = mutableMapOf<String, String>()
        if (origin != null) {
            headers[ApiHeaders.ACCESS_CONTROL_ALLOW_ORIGIN] = origin
        }
        headers[ApiHeaders.ACCESS_CONTROL_ALLOW_METHODS] = ALLOWED_METHODS
        headers[ApiHeaders.ACCESS_CONTROL_ALLOW_HEADERS] = ALLOWED_HEADERS
        headers[ApiHeaders.ACCESS_CONTROL_MAX_AGE] = "86400"
        headers[ApiHeaders.VARY] = ApiHeaders.ORIGIN

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
        val origin = request.headers[ApiHeaders.ORIGIN]
        if (origin == null || origin !in allowedOrigins) {
            return response
        }

        val headers = response.headers.toMutableMap()
        headers[ApiHeaders.ACCESS_CONTROL_ALLOW_ORIGIN] = origin
        headers[ApiHeaders.ACCESS_CONTROL_ALLOW_METHODS] = ALLOWED_METHODS
        headers[ApiHeaders.ACCESS_CONTROL_ALLOW_HEADERS] = ALLOWED_HEADERS
        headers[ApiHeaders.ACCESS_CONTROL_EXPOSE_HEADERS] =
            "${ApiHeaders.RATE_LIMIT}, ${ApiHeaders.RATE_LIMIT_REMAINING}, ${ApiHeaders.RATE_LIMIT_RESET}"
        headers[ApiHeaders.VARY] = ApiHeaders.ORIGIN

        return response.copy(headers = headers)
    }

    private fun forbidden(): ServerResponse =
        ServerResponse(
            statusCode = 403,
            statusText = "Forbidden",
            headers = mutableMapOf(
                ApiHeaders.CONTENT_TYPE to ContentType.JSON,
                ApiHeaders.VARY to ApiHeaders.ORIGIN,
            ),
            body = """{"error":"Forbidden: Origin not allowed"}""",
        )

    public companion object {
        private const val ALLOWED_METHODS = "GET, OPTIONS"
        private val ALLOWED_HEADERS = "${ApiHeaders.CONTENT_TYPE}, ${ApiHeaders.CLIENT_ID}"
        public val DEFAULT_ORIGINS: Set<String> = setOf(
            "https://shouldigooutside.now",
            "https://staging.shouldigooutside.now",
            "http://localhost:4321",
            "http://localhost:3000",
        )
    }
}
