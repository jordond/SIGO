package now.shouldigooutside.core.api.client.internal

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.client.ApiClient
import now.shouldigooutside.core.api.client.ApiUrlProvider
import now.shouldigooutside.core.api.model.ApiRoutePath
import now.shouldigooutside.core.api.model.entity.ApiResponse
import now.shouldigooutside.core.api.model.entity.VersionResponse
import now.shouldigooutside.core.api.model.entity.toModel
import now.shouldigooutside.core.model.Version
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.forecast.data.entity.ForecastRequestQuery
import now.shouldigooutside.forecast.data.entity.ForecastResponse
import now.shouldigooutside.forecast.data.entity.toModel

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
        return httpClient
            .get(url) {
                url {
                    query.forEach { (key, value) ->
                        parameters.append(key, value)
                    }
                }
            }.body<ApiResponse<ForecastResponse>>()
            .data.forecast
            .toModel()
    }
}
