package app.sigot.api.routes.forecast

import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.queryParams
import app.sigot.core.model.location.Location
import app.sigot.core.platform.http.cached
import app.sigot.core.platform.http.ok
import app.sigot.forecast.data.entity.ForecastRequestQuery
import app.sigot.forecast.data.entity.ForecastResponse
import app.sigot.forecast.data.entity.toEntity
import app.sigot.forecast.data.source.ForecastSource
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.time.Duration.Companion.minutes

class ForecastRoute(
    private val json: Json,
    private val forecastSource: ForecastSource,
) : ApiRoute {
    private val logger = Logger.withTag("ForecastRoute")
    override val path: String = "/forecast"

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response? {
        val query = request.queryParams<ForecastRequestQuery>(json = json)
        val location = Location.create(
            latitude = query.lat,
            longitude = query.lon,
            name = query.name,
        )
        logger.d { "Querying forecast for location: $location" }

        val forecast = forecastSource.forecastFor(location).toEntity()
        logger.i { "Forecast retrieved successfully for location: $location" }

        return cached(10.minutes) {
            ok(ForecastResponse(forecast = forecast))
        }
    }
}
