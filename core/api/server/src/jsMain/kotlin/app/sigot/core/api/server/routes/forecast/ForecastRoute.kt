package app.sigot.core.api.server.routes.forecast

import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.ApiRoutePath
import app.sigot.core.api.server.cache.ForecastCacheProvider
import app.sigot.core.api.server.entity.ApiResponse
import app.sigot.core.api.server.exception.BadRequestException
import app.sigot.core.api.server.queryParams
import app.sigot.core.api.server.util.cached
import app.sigot.core.api.server.util.respondJson
import app.sigot.core.api.server.util.roundCoordinate
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.entity.ForecastRequestQuery
import app.sigot.forecast.data.entity.ForecastResponse
import app.sigot.forecast.data.entity.toEntity
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.time.Duration.Companion.minutes

public class ForecastRoute(
    private val json: Json,
    private val getForecastUseCase: GetForecastUseCase,
    private val cacheProvider: ForecastCacheProvider,
) : ApiRoute {
    private val logger = Logger.withTag("ForecastRoute")
    override val path: ApiRoutePath = ApiRoutePath.Forecast

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response? {
        val query = request.queryParams<ForecastRequestQuery>(json = json)

        // US-005: Validate coordinates before rounding
        validateCoordinates(query.lat, query.lon)

        // US-001: Round coordinates for cache-friendly bucketing
        val roundedLat = query.lat.roundCoordinate()
        val roundedLon = query.lon.roundCoordinate()

        val location = Location.create(
            latitude = roundedLat,
            longitude = roundedLon,
            name = query.name,
        )
        logger.d { "Querying forecast for location: $location" }

        // US-002: Check KV cache
        val cacheKey = "forecast:$roundedLat,$roundedLon"
        val cache = cacheProvider.cache
        if (cache != null) {
            val cachedJson = cache.get(cacheKey)
            if (cachedJson != null) {
                logger.d { "Cache hit for $cacheKey" }
                return cached(10.minutes) {
                    respondJson(json = cachedJson)
                }
            }
        }

        val forecast = getForecastUseCase.forecastFor(location).getOrThrow().toEntity()
        logger.i { "Forecast retrieved successfully for location: $location" }

        val responseData = ForecastResponse(forecast = forecast)
        val responseJson = json.encodeToString(ApiResponse(data = responseData))

        // Store in KV cache
        if (cache != null) {
            cache.put(cacheKey, responseJson, ttlSeconds = 600)
        }

        return cached(10.minutes) {
            respondJson(json = responseJson)
        }
    }

    private fun validateCoordinates(
        lat: Double,
        lon: Double,
    ) {
        val errors = mutableListOf<String>()
        if (lat < -90.0 || lat > 90.0) {
            errors.add("lat must be between -90 and 90")
        }
        if (lon < -180.0 || lon > 180.0) {
            errors.add("lon must be between -180 and 180")
        }
        if (errors.isNotEmpty()) {
            throw BadRequestException(validation = errors)
        }
    }
}
