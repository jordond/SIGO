package now.shouldigooutside.forecast.data.source.visualcrossing

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import now.shouldigooutside.core.domain.forecast.ApiTokenProvider

internal interface VisualCrossingApi {
    suspend fun forecastFor(
        latitude: Double,
        longitude: Double,
    ): VCForecastResponse

    suspend fun forecastFor(name: String): VCForecastResponse
}

internal class DefaultVisualCrossingApi(
    private val httpClient: HttpClient,
    private val tokenProvider: ApiTokenProvider,
) : VisualCrossingApi {
    override suspend fun forecastFor(
        latitude: Double,
        longitude: Double,
    ): VCForecastResponse = makeRequest("$latitude,$longitude")

    override suspend fun forecastFor(name: String): VCForecastResponse = makeRequest(name)

    private suspend fun makeRequest(location: String): VCForecastResponse =
        httpClient
            .get(VisualCrossingApiSpec.BaseUrl) {
                url {
                    appendPathSegments(location)
                    VisualCrossingApiSpec.params(tokenProvider.provide()).forEach { (key, value) ->
                        parameters.append(key, value)
                    }
                }
            }.body()
}
