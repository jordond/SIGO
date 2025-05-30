package app.sigot.core.api.client.internal

import app.sigot.core.api.client.ApiClient
import app.sigot.core.api.client.ApiUrlProvider
import app.sigot.core.api.server.ApiRoutePath
import app.sigot.core.api.server.entity.ApiResponse
import app.sigot.core.api.server.entity.VersionResponse
import app.sigot.core.api.server.entity.toModel
import app.sigot.core.model.Version
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.entity.ForecastRequestQuery
import app.sigot.forecast.data.entity.ForecastResponse
import app.sigot.forecast.data.entity.toModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

internal class DefaultApiClient(
    private val urlProvider: ApiUrlProvider,
    private val httpClient: HttpClient,
    private val json: Json,
) : ApiClient {
    override suspend fun version(): Version {
        val url = urlProvider.url(ApiRoutePath.Version)
        return httpClient
            .get(url)
            .body<ApiResponse<VersionResponse>>()
            .data.version
            .toModel()
    }

    override suspend fun forecast(location: Location): Forecast {
        val query = ForecastRequestQuery(
            lat = location.latitude,
            lon = location.longitude,
            name = null,
        ).toQueryParams(json = json)

        val url = urlProvider.url(ApiRoutePath.Forecast)
        val response = httpClient.get(url) {
            url {
                query.forEach { (key, value) ->
                    parameters.append(key, value)
                }
            }
        }

        return response
            .body<ApiResponse<ForecastResponse>>()
            .data.forecast
            .toModel()
    }
}
