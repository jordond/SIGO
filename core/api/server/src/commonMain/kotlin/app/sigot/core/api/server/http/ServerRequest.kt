package app.sigot.core.api.server.http

import io.ktor.http.Headers
import io.ktor.http.HttpMethod

public data class ServerRequest(
    val url: String,
    val method: HttpMethod,
    val headers: Headers,
    val queryParameters: Map<String, String> = emptyMap(),
    val body: String? = null,
)
