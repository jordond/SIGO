package app.sigot.core.api.server.http

import io.ktor.http.HttpMethod
import org.w3c.dom.url.URL
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit

/**
 * Convert a W3C Fetch Request to a platform-agnostic ServerRequest.
 */
public fun Request.toServerRequest(): ServerRequest {
    val headers = mutableMapOf<String, String>()
    this.headers.asDynamic().forEach { value: String, key: String ->
        headers[key] = value
    }

    val url = URL(this.url)
    val queryParameters = mutableMapOf<String, String>()
    url.searchParams.asDynamic().forEach { value: String, key: String ->
        queryParameters[key] = value
    }

    return ServerRequest(
        url = this.url,
        method = HttpMethod.parse(this.method),
        headers = headers,
        queryParameters = queryParameters,
    )
}

/**
 * Convert a platform-agnostic ServerResponse to a W3C Fetch Response.
 */
public fun ServerResponse.toJsResponse(): Response {
    val headers: dynamic = js("({})")
    for ((key, value) in this.headers) {
        headers[key] = value
    }
    return Response(
        body,
        ResponseInit(
            status = statusCode.toShort(),
            statusText = statusText,
            headers = headers,
        ),
    )
}
