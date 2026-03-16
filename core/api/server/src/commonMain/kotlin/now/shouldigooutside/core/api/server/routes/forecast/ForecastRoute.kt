package now.shouldigooutside.core.api.server.routes.forecast

import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.api.model.ApiRoutePath
import now.shouldigooutside.core.api.model.entity.ApiResponse
import now.shouldigooutside.core.api.server.ApiRoute
import now.shouldigooutside.core.api.server.cache.CacheProvider
import now.shouldigooutside.core.api.server.cache.FORECAST_CACHE_TTL
import now.shouldigooutside.core.api.server.http.ServerRequest
import now.shouldigooutside.core.api.server.http.ServerResponse
import now.shouldigooutside.core.api.server.http.queryParams
import now.shouldigooutside.core.api.server.util.cached
import now.shouldigooutside.core.api.server.util.respondJson
import now.shouldigooutside.core.api.server.util.roundCoordinate
import now.shouldigooutside.core.api.server.util.validateCoordinates
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.forecast.data.entity.ForecastRequestQuery
import now.shouldigooutside.forecast.data.entity.ForecastResponse
import now.shouldigooutside.forecast.data.entity.toEntity

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
