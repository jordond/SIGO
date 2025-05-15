package app.sigot.forecast.data.source.visualcrossing

import app.sigot.forecast.domain.VisualCrossingTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments

internal interface VisualCrossingApi {
    suspend fun forecastFor(
        latitude: Double,
        longitude: Double,
    ): VCForecastResponse

    suspend fun forecastFor(name: String): VCForecastResponse
}

internal class DefaultVisualCrossingApi(
    private val httpClient: HttpClient,
    tokenProvider: VisualCrossingTokenProvider,
) : VisualCrossingApi {
    private val params = listOf(
        "key" to tokenProvider.provide(),
        "unitGroup" to "base",
        "include" to "days,hours,alerts,current,events",
    )

    override suspend fun forecastFor(
        latitude: Double,
        longitude: Double,
    ): VCForecastResponse = makeRequest("$latitude,$longitude")

    override suspend fun forecastFor(name: String): VCForecastResponse = makeRequest(name)

    private suspend fun makeRequest(location: String): VCForecastResponse =
        httpClient
            .get(BASE_URL) {
                url {
                    appendPathSegments(location)
                    params.forEach { (key, value) ->
                        parameters.append(key, value)
                    }
                }
            }.body()

    companion object {
        private const val BASE_URL =
            "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/"
    }
}
