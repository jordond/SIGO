package app.sigot.core.api.server.http

/**
 * Platform-agnostic HTTP response representation.
 */
public data class ServerResponse(
    val statusCode: Int,
    val statusText: String = "",
    val headers: MutableMap<String, String> = mutableMapOf(),
    val body: String? = null,
)
