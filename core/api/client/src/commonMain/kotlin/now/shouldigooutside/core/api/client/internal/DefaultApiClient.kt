package now.shouldigooutside.core.api.client.internal

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.client.ApiClient
import now.shouldigooutside.core.api.client.ApiResult
import now.shouldigooutside.core.api.client.ApiUrlProvider
import now.shouldigooutside.core.api.model.ApiRoutePath
import now.shouldigooutside.core.api.model.entity.ApiResponse
import now.shouldigooutside.core.api.model.entity.VersionResponse
import now.shouldigooutside.core.api.model.entity.toModel
import now.shouldigooutside.core.api.model.http.ApiHeaders
import now.shouldigooutside.core.api.model.http.RateLimit
import now.shouldigooutside.core.model.Version
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.forecast.data.entity.ForecastRequestQuery
import now.shouldigooutside.forecast.data.entity.ForecastResponse
import now.shouldigooutside.forecast.data.entity.toModel
import kotlin.time.Instant

internal class DefaultApiClient(
    private val urlProvider: ApiUrlProvider,
    private val httpClient: HttpClient,
    private val json: Json,
) : ApiClient {
    override suspend fun version(): ApiResult<Version> {
        val url = urlProvider.url(ApiRoutePath.Version)
        val response = httpClient.get(url)
        val version = response
            .body<ApiResponse<VersionResponse>>()
            .data.version
            .toModel()
        return ApiResult(data = version, rateLimit = response.rateLimit())
    }

    override suspend fun forecast(location: Location): ApiResult<Forecast> {
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

        val forecast = response
            .body<ApiResponse<ForecastResponse>>()
            .data.forecast
            .toModel()
        return ApiResult(data = forecast, rateLimit = response.rateLimit())
    }

    private fun HttpResponse.rateLimit(): RateLimit? {
        val limit = headers[ApiHeaders.RATE_LIMIT]?.toIntOrNull() ?: return null
        val remaining = headers[ApiHeaders.RATE_LIMIT_REMAINING]?.toIntOrNull() ?: return null
        val resetEpoch = headers[ApiHeaders.RATE_LIMIT_RESET]?.toLongOrNull() ?: return null
        return RateLimit(
            limit = limit,
            remaining = remaining,
            resetAt = Instant.fromEpochSeconds(resetEpoch),
        )
    }
}
