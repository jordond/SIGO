package app.sigot.core.api.server

import app.sigot.core.api.server.util.getQueryParams
import app.sigot.core.platform.di.defaultJson
import app.sigot.core.platform.http.BadRequestException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.w3c.fetch.Request
import org.w3c.fetch.Response

/**
 * Represents an API route with a specific path and typed request/response handling
 */
public interface ApiRoute {
    public val path: String

    /**
     * Handle GET requests with typed response
     */
    public suspend fun get(
        request: Request,
        parameters: Map<String, String> = emptyMap(),
    ): Response? = throw NotImplementedError()

    /**
     * Handle POST requests with typed body input and response
     */
    public suspend fun post(
        request: Request,
        parameters: Map<String, String> = emptyMap(),
    ): Response? = throw NotImplementedError()

    /**
     * Handle PUT requests with typed body input and response
     */
    public suspend fun put(
        request: Request,
        parameters: Map<String, String> = emptyMap(),
    ): Response? = throw NotImplementedError()

    /**
     * Handle DELETE requests with typed response
     */
    public suspend fun delete(
        request: Request,
        parameters: Map<String, String> = emptyMap(),
    ): Response? = throw NotImplementedError()
}

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T : @Serializable Any> Request.queryParams(json: Json = defaultJson): T {
    try {
        json.configuration.isLenient
        val params = getQueryParams(this)
        val mapJson = json.encodeToString(params)
        return json.decodeFromString<T>(mapJson)
    } catch (cause: MissingFieldException) {
        throw BadRequestException(validation = cause.missingFields)
    }
}
