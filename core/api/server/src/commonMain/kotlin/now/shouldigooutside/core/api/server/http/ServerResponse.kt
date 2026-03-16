package now.shouldigooutside.core.api.server.http

import io.ktor.http.Headers
import io.ktor.http.headersOf

public data class ServerResponse(
    val statusCode: Int,
    val statusText: String = "",
    val headers: Headers = headersOf(),
    val body: String? = null,
)
