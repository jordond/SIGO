package app.sigot.core.api.server.routes.forecast.score

import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.ApiRoutePath
import app.sigot.core.api.server.queryParams
import app.sigot.core.api.server.util.cached
import app.sigot.core.api.server.util.ok
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.ScoreCalculator
import app.sigot.forecast.data.entity.ForecastScoreRequestQuery
import app.sigot.forecast.data.entity.ForecastScoreResponse
import app.sigot.forecast.data.entity.toEntity
import app.sigot.forecast.data.entity.toModels
import kotlinx.serialization.json.Json
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.time.Duration.Companion.minutes

public class ForecastScoreRoute(
    private val json: Json,
    private val getForecastUseCase: GetForecastUseCase,
    private val scoreCalculator: ScoreCalculator,
) : ApiRoute {
    override val path: ApiRoutePath = ApiRoutePath.ForecastScore

    override suspend fun get(
        request: Request,
        parameters: Map<String, String>,
    ): Response? {
        val (location, preferences) = request.queryParams<ForecastScoreRequestQuery>(json).toModels()
        val forecast = getForecastUseCase.forecastFor(location).getOrThrow()
        val score = scoreCalculator.calculate(forecast, preferences).toEntity()
        val response = ForecastScoreResponse(forecast = forecast.toEntity(), score = score)
        return cached(10.minutes) {
            ok(response)
        }
    }
}
