package app.sigot.core.api.server.http

/**
 * Platform-agnostic HTTP response representation.
 */
public data class ServerResponse(
    val statusCode: Int,
    val statusText: String = "",
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
)
