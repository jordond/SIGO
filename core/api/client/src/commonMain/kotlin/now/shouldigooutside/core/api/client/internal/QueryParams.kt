package now.shouldigooutside.core.api.client.internal

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.serializer
import now.shouldigooutside.core.platform.di.defaultJson

internal inline fun <reified T> T.toQueryParams(json: Json = defaultJson): Map<String, String> {
    val jsonElement = json.encodeToJsonElement(serializer<T>(), this)
    return jsonElement.toQueryParams()
}

private fun JsonElement.toQueryParams(prefix: String = ""): Map<String, String> =
    when (this) {
        is JsonNull -> {
            emptyMap()
        }
        is JsonPrimitive -> {
            if (prefix.isEmpty()) {
                emptyMap()
            } else {
                mapOf(prefix to this.content)
            }
        }
        is JsonObject -> {
            this.entries
                .flatMap { (key, value) ->
                    val newPrefix = if (prefix.isEmpty()) key else "$prefix.$key"
                    value.toQueryParams(newPrefix).entries
                }.associate { it.key to it.value }
        }
        is JsonArray -> {
            this
                .mapIndexed { index, value ->
                    val newPrefix = if (prefix.isEmpty()) "[$index]" else "$prefix[$index]"
                    value.toQueryParams(newPrefix)
                }.fold(emptyMap()) { acc, map -> acc + map }
        }
    }
