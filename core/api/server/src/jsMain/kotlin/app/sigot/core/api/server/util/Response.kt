package app.sigot.core.api.server.util

import app.sigot.core.api.server.entity.ApiError
import app.sigot.core.api.server.entity.ApiResponse
import app.sigot.core.platform.di.defaultJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit
import kotlin.time.Duration

public fun respondText(
    text: String,
    statusText: String = "OK",
    status: Int = 200,
): Response {
    val headers: dynamic = object {}
    headers["content-type"] = "text/plain"
    val init = ResponseInit(status = status.toShort(), statusText = statusText, headers = headers)
    return Response(text, init)
}

public fun respondJson(
    json: String,
    statusText: String = "OK",
    status: Int = 200,
): Response {
    val headers: dynamic = object {}
    headers["content-type"] = "application/json"
    val init = ResponseInit(status = status.toShort(), statusText = statusText, headers = headers)
    return Response(json, init)
}

public fun respondJson(
    data: Map<String, Any?>,
    meta: Map<String, Any?> = emptyMap(),
    statusText: String = "OK",
    status: Int = 200,
    json: Json = defaultJson,
): Response {
    val map = data.mapValues { it.value.toString() }
    val response = ApiResponse(data = map, meta = meta.forJson())
    val json = json.encodeToString(response)
    return respondJson(json, statusText, status)
}

public inline fun <reified T : @Serializable Any?> respondJson(
    data: T,
    meta: Map<String, Any?> = emptyMap(),
    statusText: String = "OK",
    status: Int = 200,
    json: Json = defaultJson,
): Response {
    val response = ApiResponse(data = data, meta = meta.forJson())
    val json = json.encodeToString(response)
    return respondJson(json, statusText, status)
}

public inline fun <reified T : @Serializable Any?> ok(
    data: T,
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response =
    respondJson(
        data = data,
        meta = meta,
        status = 200,
        statusText = "OK",
        json = json,
    )

public fun noContent(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response =
    respondJson(
        data = Unit,
        meta = meta,
        status = 204,
        statusText = "No Content",
        json = json,
    )

public fun badRequest(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response =
    respondJson(
        data = Unit,
        meta = meta,
        status = 400,
        statusText = "Bad Request",
        json = json,
    )

public fun notFound(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response =
    respondJson(
        data = Unit,
        meta = meta,
        status = 404,
        statusText = "Not Found",
        json = json,
    )

public fun methodNotAllowed(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response =
    respondJson(
        data = Unit,
        meta = meta,
        status = 405,
        statusText = "Method Not Allowed",
        json = json,
    )

public fun serverError(
    cause: Throwable,
    message: String = cause.message ?: "Unknown error",
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response {
    val data = ApiError(error = message, meta = meta.forJson())
    val json = json.encodeToString(data)
    return respondJson(
        json = json,
        status = 500,
        statusText = "Internal Server Error",
    )
}

public fun unauthorized(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response =
    respondJson(
        data = Unit,
        meta = meta,
        status = 401,
        statusText = "Unauthorized",
        json = json,
    )

public fun forbidden(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response =
    respondJson(
        data = Unit,
        meta = meta,
        status = 403,
        statusText = "Forbidden",
        json = json,
    )

public fun tooManyRequests(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): Response =
    respondJson(
        data = Unit,
        meta = meta,
        status = 429,
        statusText = "Too Many Requests",
        json = json,
    )

public fun cached(
    age: Duration,
    block: () -> Response,
): Response {
    val response = block().apply {
        headers.append("cache-control", "max-age=${age.inWholeSeconds}")
    }
    return response
}

@PublishedApi
internal fun Map<String, Any?>.forJson(): Map<String, String> = mapValues { it.value.toString() }
