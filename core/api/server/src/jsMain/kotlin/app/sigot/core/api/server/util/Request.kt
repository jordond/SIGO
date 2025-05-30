package app.sigot.core.api.server.util

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
