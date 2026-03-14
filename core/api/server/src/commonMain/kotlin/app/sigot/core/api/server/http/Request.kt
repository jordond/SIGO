package app.sigot.core.api.server.http

import app.sigot.core.api.server.exception.BadRequestException
import app.sigot.core.platform.di.defaultJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T : @Serializable Any> ServerRequest.queryParams(json: Json = defaultJson): T {
    try {
        val mapJson = json.encodeToString(queryParameters)
        return json.decodeFromString<T>(mapJson)
    } catch (cause: MissingFieldException) {
        throw BadRequestException(validation = cause.missingFields)
    }
}
