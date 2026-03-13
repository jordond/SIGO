package app.sigot.core.api.server.routes.forecast.score

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
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.core.model.location.Location
import app.sigot.core.model.preferences.Preferences
import app.sigot.forecast.data.entity.ForecastScoreRequestQuery
import app.sigot.forecast.data.entity.ForecastScoreResponse
import app.sigot.forecast.data.entity.toEntity
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.time.Duration.Companion.minutes

public class ForecastScoreRoute(
    private val json: Json,
    private val getForecastUseCase: GetForecastUseCase,
    private val scoreCalculator: ScoreCalculator,
    private val cacheProvider: ForecastCacheProvider,
) : ApiRoute {
    private val logger = Logger.withTag("ForecastScoreRoute")
    override val path: ApiRoutePath = ApiRoutePath.ForecastScore

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response? {
        val query = request.queryParams<ForecastScoreRequestQuery>(json)

        validateRequest(query)

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
            append("score:$roundedLat,$roundedLon")
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
                return cached(10.minutes) {
                    respondJson(json = cachedJson)
                }
            }
        }

        val forecast = getForecastUseCase.forecastFor(location).getOrThrow()
        val score = scoreCalculator.calculate(forecast, preferences).toEntity()
        val responseData = ForecastScoreResponse(forecast = forecast.toEntity(), score = score)
        val responseJson = json.encodeToString(ApiResponse(data = responseData))

        if (cache != null) {
            cache.put(cacheKey, responseJson, ttlSeconds = 600)
        }

        return cached(10.minutes) {
            respondJson(json = responseJson)
        }
    }

    private fun validateRequest(query: ForecastScoreRequestQuery) {
        val errors = mutableListOf<String>()

        if (query.lat < -90.0 || query.lat > 90.0) {
            errors.add("lat must be between -90 and 90")
        }
        if (query.lon < -180.0 || query.lon > 180.0) {
            errors.add("lon must be between -180 and 180")
        }
        val maxTemp = query.maxTemp
        if (maxTemp != null && (maxTemp < -100 || maxTemp > 100)) {
            errors.add("max_temp must be between -100 and 100")
        }
        val minTemp = query.minTemp
        if (minTemp != null && (minTemp < -100 || minTemp > 100)) {
            errors.add("min_temp must be between -100 and 100")
        }
        val maxWind = query.maxWind
        if (maxWind != null && maxWind < 0) {
            errors.add("max_wind must be >= 0")
        }

        if (errors.isNotEmpty()) {
            throw BadRequestException(validation = errors)
        }
    }
}
