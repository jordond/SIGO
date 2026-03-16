package now.shouldigooutside.core.api.server.http

import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpMethod
import org.w3c.dom.url.URL
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit

public fun Request.toServerRequest(): ServerRequest {
    val headers = headers.toKtorHeaders()
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

private fun org.w3c.fetch.Headers.toKtorHeaders(): Headers =
    HeadersBuilder()
        .apply {
            asDynamic().forEach { value: String, key: String ->
                append(key, value)
            }
        }.build()

public fun ServerResponse.toJsResponse(): Response {
    val jsHeaders: dynamic = js("({})")
    this.headers.forEach { name, values ->
        jsHeaders[name] = values.joinToString(", ")
    }
    return Response(
        body,
        ResponseInit(
            status = statusCode.toShort(),
            statusText = statusText,
            headers = jsHeaders,
        ),
    )
}
