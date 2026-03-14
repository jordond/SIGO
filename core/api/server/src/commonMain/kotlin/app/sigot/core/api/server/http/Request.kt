package app.sigot.core.api.server.http

import app.sigot.core.api.server.exception.BadRequestException
import app.sigot.core.platform.di.defaultJson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T : @Serializable Any> ServerRequest.queryParams(json: Json = defaultJson): T {
    try {
        val jsonElement = json.encodeToJsonElement(queryParameters)
        return json.decodeFromJsonElement<T>(jsonElement)
    } catch (cause: MissingFieldException) {
        throw BadRequestException(validation = cause.missingFields)
    }
}
