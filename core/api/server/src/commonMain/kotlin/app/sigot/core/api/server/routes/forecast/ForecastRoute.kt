package app.sigot.core.api.server.routes.forecast

import app.sigot.core.api.model.ApiRoutePath
import app.sigot.core.api.model.entity.ApiResponse
import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.cache.CacheProvider
import app.sigot.core.api.server.cache.FORECAST_CACHE_TTL
import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse
import app.sigot.core.api.server.http.queryParams
import app.sigot.core.api.server.util.cached
import app.sigot.core.api.server.util.respondJson
import app.sigot.core.api.server.util.roundCoordinate
import app.sigot.core.api.server.util.validateCoordinates
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.entity.ForecastRequestQuery
import app.sigot.forecast.data.entity.ForecastResponse
import app.sigot.forecast.data.entity.toEntity
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json

public class ForecastRoute(
    private val json: Json,
    private val getForecastUseCase: GetForecastUseCase,
    private val cacheProvider: CacheProvider,
) : ApiRoute {
    private val logger = Logger.withTag("ForecastRoute")
    override val path: ApiRoutePath = ApiRoutePath.Forecast

    override suspend fun get(
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        val query = request.queryParams<ForecastRequestQuery>(json = json)

        validateCoordinates(query.lat, query.lon)

        val roundedLat = query.lat.roundCoordinate()
        val roundedLon = query.lon.roundCoordinate()

        val location = Location.create(
            latitude = roundedLat,
            longitude = roundedLon,
            name = query.name,
        )
        logger.d { "Querying forecast for location: $location" }

        val cacheKey = cacheKey(roundedLat, roundedLon)
        val cache = cacheProvider.cache
        if (cache != null) {
            val cachedJson = cache.get(cacheKey)
            if (cachedJson != null) {
                logger.d { "Cache hit for $cacheKey" }
                return cached(FORECAST_CACHE_TTL) {
                    respondJson(json = cachedJson)
                }
            }
        }

        val forecast = getForecastUseCase.forecastFor(location).getOrThrow().toEntity()
        logger.i { "Forecast retrieved successfully for location: $location" }

        val responseData = ForecastResponse(forecast = forecast)
        val responseJson = json.encodeToString(ApiResponse(data = responseData))

        cache?.put(cacheKey, responseJson, ttl = FORECAST_CACHE_TTL)

        return cached(FORECAST_CACHE_TTL) {
            respondJson(json = responseJson)
        }
    }

    private companion object {
        const val CACHE_KEY_PREFIX = "v1:forecast"

        fun cacheKey(
            lat: Double,
            lon: Double,
        ): String = "$CACHE_KEY_PREFIX:$lat,$lon"
    }
}
