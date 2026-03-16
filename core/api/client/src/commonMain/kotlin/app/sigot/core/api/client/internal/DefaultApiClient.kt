package app.sigot.core.api.client.internal

import app.sigot.core.api.client.ApiClient
import app.sigot.core.api.client.ApiResult
import app.sigot.core.api.client.ApiUrlProvider
import app.sigot.core.api.model.ApiRoutePath
import app.sigot.core.api.model.entity.ApiResponse
import app.sigot.core.api.model.entity.VersionResponse
import app.sigot.core.api.model.entity.toModel
import app.sigot.core.api.model.http.ApiHeaders
import app.sigot.core.api.model.http.RateLimit
import app.sigot.core.model.Version
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.entity.ForecastRequestQuery
import app.sigot.forecast.data.entity.ForecastResponse
import app.sigot.forecast.data.entity.toModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.Json
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
