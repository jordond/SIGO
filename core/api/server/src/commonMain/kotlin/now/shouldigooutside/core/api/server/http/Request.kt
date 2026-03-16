package now.shouldigooutside.core.api.server.http

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import now.shouldigooutside.core.api.server.exception.BadRequestException
import now.shouldigooutside.core.platform.di.defaultJson

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T : @Serializable Any> ServerRequest.queryParams(json: Json = defaultJson): T {
    try {
        val jsonElement = json.encodeToJsonElement(queryParameters)
        return json.decodeFromJsonElement<T>(jsonElement)
    } catch (cause: MissingFieldException) {
        throw BadRequestException(validation = cause.missingFields)
    }
}
