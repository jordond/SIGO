package app.sigot.api.util

import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.w3c.dom.url.URL
import org.w3c.fetch.Request

/**
 * Extract query parameters from request
 */
@PublishedApi
internal fun getQueryParams(request: Request): Map<String, String> {
    val url = URL(request.url)
    val params = mutableMapOf<String, String>()
    url.searchParams.asDynamic().forEach { value: String, key: String ->
        params[key] = value
    }
    return params
}

/**
 * Parse JSON body to typed object
 */
internal suspend inline fun <reified T> parseJsonBody(request: Request): T {
    val bodyText = request.text().await()
    return Json.decodeFromString<T>(bodyText)
}
