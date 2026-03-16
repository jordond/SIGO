package app.sigot.core.api.server.util

import app.sigot.core.api.model.entity.ApiError
import app.sigot.core.api.model.entity.ApiResponse
import app.sigot.core.api.server.http.ContentType
import app.sigot.core.api.server.http.ServerResponse
import app.sigot.core.platform.di.defaultJson
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json
import kotlin.time.Duration

public fun respondText(
    text: String,
    statusText: String = "OK",
    status: Int = 200,
): ServerResponse =
    ServerResponse(
        statusCode = status,
        statusText = statusText,
        headers = headersOf(HttpHeaders.ContentType, ContentType.TEXT_PLAIN),
        body = text,
    )

public fun respondJson(
    json: String,
    statusText: String = "OK",
    status: Int = 200,
): ServerResponse =
    ServerResponse(
        statusCode = status,
        statusText = statusText,
        headers = headersOf(HttpHeaders.ContentType, ContentType.JSON),
        body = json,
    )

public fun respondJson(
    data: Map<String, Any?>,
    meta: Map<String, Any?> = emptyMap(),
    statusText: String = "OK",
    status: Int = 200,
    json: Json = defaultJson,
): ServerResponse {
    val map = data.mapValues { it.value.toString() }
    val response = ApiResponse(data = map, meta = meta.forJson())
    val encoded = json.encodeToString(response)
    return respondJson(encoded, statusText, status)
}

public inline fun <reified T> respondJson(
    data: T,
    meta: Map<String, Any?> = emptyMap(),
    statusText: String = "OK",
    status: Int = 200,
    json: Json = defaultJson,
): ServerResponse {
    val response = ApiResponse(data = data, meta = meta.forJson())
    val encoded = json.encodeToString(response)
    return respondJson(encoded, statusText, status)
}

public inline fun <reified T> ok(
    data: T,
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): ServerResponse =
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
): ServerResponse =
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
): ServerResponse =
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
): ServerResponse =
    respondJson(
        data = Unit,
        meta = meta,
        status = 404,
        statusText = "Not Found",
        json = json,
    )

public fun methodNotAllowed(
    method: HttpMethod,
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): ServerResponse =
    respondJson(
        data = Unit,
        meta = mapOf("method" to method.value) + meta,
        status = 405,
        statusText = "Method Not Allowed",
        json = json,
    )

public fun serverError(
    message: String = "An internal error occurred",
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): ServerResponse {
    val data = ApiError(error = message, meta = meta.forJson())
    val encoded = json.encodeToString(data)
    return respondJson(
        json = encoded,
        status = 500,
        statusText = "Internal Server Error",
    )
}

public fun unauthorized(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): ServerResponse =
    respondJson(
        data = Unit,
        meta = meta,
        status = 401,
        statusText = "Unauthorized",
        json = json,
    )

public fun tooManyRequests(
    meta: Map<String, Any?> = emptyMap(),
    json: Json = defaultJson,
): ServerResponse =
    respondJson(
        data = Unit,
        meta = meta,
        status = 429,
        statusText = "Too Many Requests",
        json = json,
    )

public fun cached(
    age: Duration,
    block: () -> ServerResponse,
): ServerResponse {
    val response = block()
    return response.copy(
        headers = HeadersBuilder()
            .apply {
                appendAll(response.headers)
                append(HttpHeaders.CacheControl, "max-age=${age.inWholeSeconds}")
            }.build(),
    )
}

@PublishedApi
internal fun Map<String, Any?>.forJson(): Map<String, String> = mapValues { it.value.toString() }
