package now.shouldigooutside.core.api.server.http

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.route
import now.shouldigooutside.core.api.server.ApiRouter
import kotlin.coroutines.cancellation.CancellationException

/**
 * Mount the [ApiRouter] to handle all incoming requests under [path].
 */
public fun Route.mountApiRouter(
    router: ApiRouter,
    path: String = "/",
) {
    route("$path{...}") {
        handle {
            val request = call.toServerRequest()
            val response = router.handle(request)
            response.writeTo(call)
        }
    }
}

private suspend fun ApplicationCall.toServerRequest(): ServerRequest {
    val headers = request.headers
    val queryParameters = mutableMapOf<String, String>()
    request.queryParameters.forEach { name, values ->
        if (values.isNotEmpty()) {
            queryParameters[name] = values.first()
        }
    }

    val body = try {
        receiveText().takeIf { it.isNotEmpty() }
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }

    val scheme = request.local.scheme
    val host = request.local.serverHost
    val port = request.local.serverPort
    val uri = request.uri
    val fullUrl = "$scheme://$host:$port$uri"

    return ServerRequest(
        url = fullUrl,
        method = HttpMethod.parse(request.httpMethod.value),
        headers = headers,
        queryParameters = queryParameters,
        body = body,
    )
}

private suspend fun ServerResponse.writeTo(call: ApplicationCall) {
    headers.forEach { name, values ->
        if (!name.equals(HttpHeaders.ContentType, ignoreCase = true)) {
            values.forEach { call.response.header(name, it) }
        }
    }

    val contentType = headers[HttpHeaders.ContentType]
        ?.let { ContentType.parse(it) }
        ?: ContentType.Text.Plain

    call.respondText(
        text = body ?: "",
        contentType = contentType,
        status = HttpStatusCode.fromValue(statusCode),
    )
}
