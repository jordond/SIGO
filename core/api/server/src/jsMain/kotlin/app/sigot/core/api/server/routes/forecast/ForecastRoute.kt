package app.sigot.core.api.server.routes.forecast

import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.ApiRoutePath
import app.sigot.core.api.server.queryParams
import app.sigot.core.api.server.util.cached
import app.sigot.core.api.server.util.ok
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.forecast.data.entity.ForecastRequestQuery
import app.sigot.forecast.data.entity.ForecastResponse
import app.sigot.forecast.data.entity.toEntity
import app.sigot.forecast.data.entity.toLocation
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.time.Duration.Companion.minutes

public class ForecastRoute(
    private val json: Json,
    private val getForecastUseCase: GetForecastUseCase,
) : ApiRoute {
    private val logger = Logger.withTag("ForecastRoute")
    override val path: ApiRoutePath = ApiRoutePath.Forecast

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response? {
        val location = request.queryParams<ForecastRequestQuery>(json = json).toLocation()
        logger.d { "Querying forecast for location: $location" }

        val forecast = getForecastUseCase.forecastFor(location).getOrThrow().toEntity()
        logger.i { "Forecast retrieved successfully for location: $location" }

        return cached(10.minutes) {
            ok(ForecastResponse(forecast = forecast))
        }
    }
}
