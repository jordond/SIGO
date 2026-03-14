package app.sigot.core.api.server.cors

import app.sigot.core.api.server.http.ApiHeaders
import app.sigot.core.api.server.http.ContentType
import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf

public class DefaultCorsHandler(
    private val allowedOrigins: Set<String> = DEFAULT_ORIGINS,
) : CorsHandler {
    override fun validateOrigin(request: ServerRequest): ServerResponse? {
        val origin = request.headers[HttpHeaders.Origin] ?: return null
        if (origin !in allowedOrigins) {
            return forbidden()
        }
        return null
    }

    override fun preflight(request: ServerRequest): ServerResponse {
        val origin = request.headers[HttpHeaders.Origin]
        if (origin != null && origin !in allowedOrigins) {
            return forbidden()
        }

        val headers = HeadersBuilder()
            .apply {
                if (origin != null) {
                    append(HttpHeaders.AccessControlAllowOrigin, origin)
                }
                append(HttpHeaders.AccessControlAllowMethods, ALLOWED_METHODS)
                append(HttpHeaders.AccessControlAllowHeaders, ALLOWED_HEADERS)
                append(HttpHeaders.AccessControlMaxAge, "86400")
                append(HttpHeaders.Vary, HttpHeaders.Origin)
            }.build()

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
        val origin = request.headers[HttpHeaders.Origin]
        if (origin == null || origin !in allowedOrigins) {
            return response
        }

        val headers = HeadersBuilder()
            .apply {
                appendAll(response.headers)
                append(HttpHeaders.AccessControlAllowOrigin, origin)
                append(HttpHeaders.AccessControlAllowMethods, ALLOWED_METHODS)
                append(HttpHeaders.AccessControlAllowHeaders, ALLOWED_HEADERS)
                append(
                    HttpHeaders.AccessControlExposeHeaders,
                    "${ApiHeaders.RATE_LIMIT}, ${ApiHeaders.RATE_LIMIT_REMAINING}, ${ApiHeaders.RATE_LIMIT_RESET}",
                )
                append(HttpHeaders.Vary, HttpHeaders.Origin)
            }.build()

        return response.copy(headers = headers)
    }

    private fun forbidden(): ServerResponse =
        ServerResponse(
            statusCode = 403,
            statusText = "Forbidden",
            headers = headersOf(
                HttpHeaders.ContentType to listOf(ContentType.JSON),
                HttpHeaders.Vary to listOf(HttpHeaders.Origin),
            ),
            body = """{"error":"Forbidden: Origin not allowed"}""",
        )

    public companion object {
        private const val ALLOWED_METHODS = "GET, OPTIONS"
        private val ALLOWED_HEADERS = "${HttpHeaders.ContentType}, ${ApiHeaders.CLIENT_ID}"
        public val DEFAULT_ORIGINS: Set<String> = setOf(
            "https://shouldigooutside.now",
            "https://staging.shouldigooutside.now",
            "http://localhost:4321",
            "http://localhost:3000",
        )
    }
}
