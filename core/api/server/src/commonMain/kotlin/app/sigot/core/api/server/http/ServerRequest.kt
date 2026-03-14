package app.sigot.core.api.server.http

import io.ktor.http.HttpMethod

/**
 * Platform-agnostic HTTP request representation.
 */
public data class ServerRequest(
    val url: String,
    val method: HttpMethod,
    val headers: Map<String, String>,
    val queryParameters: Map<String, String> = emptyMap(),
    val body: String? = null,
)
