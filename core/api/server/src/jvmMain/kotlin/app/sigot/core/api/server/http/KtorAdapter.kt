package app.sigot.core.api.server.http

import app.sigot.core.api.server.ApiRouter
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.server.response.header
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

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

/**
 * Convert a Ktor [io.ktor.server.request.ApplicationRequest] to a [ServerRequest].
 */
private suspend fun io.ktor.server.application.ApplicationCall.toServerRequest(): ServerRequest {
    val headers = mutableMapOf<String, String>()
    request.headers.forEach { name, values ->
        if (values.isNotEmpty()) {
            headers[name] = values.first()
        }
    }

    val queryParameters = mutableMapOf<String, String>()
    request.queryParameters.forEach { name, values ->
        if (values.isNotEmpty()) {
            queryParameters[name] = values.first()
        }
    }

    val body = try {
        receiveText().takeIf { it.isNotEmpty() }
    } catch (e: kotlin.coroutines.cancellation.CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }

    // Build a full URL from the request for compatibility with DefaultApiRouter.extractPath()
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

/**
 * Write a [ServerResponse] back to a Ktor [io.ktor.server.application.ApplicationCall].
 */
private suspend fun ServerResponse.writeTo(call: io.ktor.server.application.ApplicationCall) {
    for ((name, value) in headers) {
        if (!name.equals("Content-Type", ignoreCase = true)) {
            call.response.header(name, value)
        }
    }

    val contentType = headers["Content-Type"]
        ?.let {
            io.ktor.http.ContentType
                .parse(it)
        }
        ?: io.ktor.http.ContentType.Text.Plain

    call.respondText(
        text = body ?: "",
        contentType = contentType,
        status = HttpStatusCode.fromValue(statusCode),
    )
}
