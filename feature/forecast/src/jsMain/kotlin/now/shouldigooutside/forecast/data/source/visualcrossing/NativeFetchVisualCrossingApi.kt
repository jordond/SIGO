package now.shouldigooutside.forecast.data.source.visualcrossing

import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.domain.forecast.ApiTokenProvider
import org.w3c.fetch.Response
import kotlin.js.Promise

// Bypasses ktor-client-js because its per-call InvokeOnCancelling handler
// captures the request-bound AbortController, which workerd rejects when
// the continuation resolves on a foreign request context under concurrent
// load (CF error 1101).
internal class NativeFetchVisualCrossingApi(
    private val tokenProvider: ApiTokenProvider,
    private val json: Json,
) : VisualCrossingApi {
    override suspend fun forecastFor(
        latitude: Double,
        longitude: Double,
    ): VCForecastResponse = makeRequest("$latitude,$longitude")

    override suspend fun forecastFor(name: String): VCForecastResponse = makeRequest(name)

    private suspend fun makeRequest(location: String): VCForecastResponse {
        val url = buildUrl(location)
        val response = jsFetch(url).await()
        if (!response.ok) {
            val body = runCatching { response.text().await() }.getOrNull().orEmpty()
            throw RuntimeException("VisualCrossing HTTP ${response.status.toInt()}: $body")
        }
        val body = response.text().await()
        return json.decodeFromString(VCForecastResponse.serializer(), body)
    }

    private fun buildUrl(location: String): String =
        buildString {
            append(VisualCrossingApiSpec.BaseUrl)
            append(encodeURIComponent(location))
            append('?')
            append("key=").append(encodeURIComponent(tokenProvider.provide()))
            append(StaticQuery)
        }
}

private val StaticQuery: String =
    "&unitGroup=" + encodeURIComponent(VisualCrossingApiSpec.UnitGroup) +
        "&include=" + encodeURIComponent(VisualCrossingApiSpec.Include) +
        "&elements=" + encodeURIComponent(VisualCrossingApiSpec.Elements)

@JsName("fetch")
private external fun jsFetch(input: String): Promise<Response>

@JsName("encodeURIComponent")
private external fun encodeURIComponent(value: String): String
