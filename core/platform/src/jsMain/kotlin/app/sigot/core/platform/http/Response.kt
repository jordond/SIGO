package app.sigot.core.platform.http

import app.sigot.core.platform.di.defaultJson
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.fetch.Response
import org.w3c.fetch.ResponseInit

public class BadRequestException(
    message: String = "Bad Request",
    public val validation: List<String> = emptyList(),
) : Exception(message)

@Serializable
public data class ApiResponse<T : @Serializable Any?>(
    val data: T,
    val meta: Map<String, String> = emptyMap(),
) {
    public constructor(data: T, meta: Map<String, Any?>) : this(data, meta.forJson())
}

@Serializable
public data class ApiError(
    val error: String,
    val meta: Map<String, String> = emptyMap(),
)

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
    val response = ApiResponse(data = map, meta = meta)
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
    val response = ApiResponse(data = data, meta = meta)
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
    validation: Map<String, String> = emptyMap(),
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

private fun Map<String, Any?>.forJson() = mapValues { it.value.toString() }
