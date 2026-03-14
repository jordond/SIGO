package app.sigot.core.api.server.routes.forecast.score

import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.ApiRoutePath
import app.sigot.core.api.server.cache.CacheProvider
import app.sigot.core.api.server.cache.FORECAST_CACHE_TTL
import app.sigot.core.api.server.entity.ApiResponse
import app.sigot.core.api.server.exception.BadRequestException
import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse
import app.sigot.core.api.server.http.queryParams
import app.sigot.core.api.server.util.cached
import app.sigot.core.api.server.util.respondJson
import app.sigot.core.api.server.util.roundCoordinate
import app.sigot.core.api.server.util.validateCoordinates
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.model.location.Location
import app.sigot.core.model.preferences.Preferences
import app.sigot.forecast.data.entity.ForecastScoreRequestQuery
import app.sigot.forecast.data.entity.ForecastScoreResponse
import app.sigot.forecast.data.entity.toEntity
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json

public class ForecastScoreRoute(
    private val json: Json,
    private val getForecastUseCase: GetForecastUseCase,
    private val scoreCalculator: ScoreCalculator,
    private val cacheProvider: CacheProvider,
) : ApiRoute {
    private val logger = Logger.withTag("ForecastScoreRoute")
    override val path: ApiRoutePath = ApiRoutePath.ForecastScore

    override suspend fun get(
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        val query = request.queryParams<ForecastScoreRequestQuery>(json)

        validateCoordinates(query.lat, query.lon)
        validateScoreParams(query)

        val roundedLat = query.lat.roundCoordinate()
        val roundedLon = query.lon.roundCoordinate()

        val location = Location.create(roundedLat, roundedLon, query.name)
        val defaults = Preferences.default
        val preferences = Preferences(
            units = defaults.units,
            minTemperature = query.minTemp ?: defaults.minTemperature,
            maxTemperature = query.maxTemp ?: defaults.maxTemperature,
            windSpeed = query.maxWind ?: defaults.windSpeed,
            rain = query.allowRain ?: defaults.rain,
            snow = query.allowSnow ?: defaults.snow,
            includeApparentTemperature = defaults.includeApparentTemperature,
        )

        val cacheKey = buildString {
            append("v1:score:$roundedLat,$roundedLon")
            append(":${query.maxTemp}")
            append(":${query.minTemp}")
            append(":${query.maxWind}")
            append(":${query.allowRain}")
            append(":${query.allowSnow}")
        }

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

        val forecast = getForecastUseCase.forecastFor(location).getOrThrow()
        val score = scoreCalculator.calculate(forecast, preferences).toEntity()
        val responseData = ForecastScoreResponse(forecast = forecast.toEntity(), score = score)
        val responseJson = json.encodeToString(ApiResponse(data = responseData))
        cache?.put(cacheKey, responseJson, ttl = FORECAST_CACHE_TTL)

        return cached(FORECAST_CACHE_TTL) {
            respondJson(json = responseJson)
        }
    }

    private fun validateScoreParams(query: ForecastScoreRequestQuery) {
        val errors = buildList {
            query.maxTemp
                ?.takeIf { it !in -100..100 }
                ?.let { add("max_temp must be between -100 and 100") }

            query.minTemp
                ?.takeIf { it !in -100..100 }
                ?.let { add("min_temp must be between -100 and 100") }

            query.maxWind
                ?.takeIf { it < 0 }
                ?.let { add("max_wind must be >= 0") }
        }

        if (errors.isNotEmpty()) {
            throw BadRequestException(validation = errors)
        }
    }
}
