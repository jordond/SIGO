package app.sigot.forecast.data.source.visualcrossing

import app.sigot.core.foundation.NowProvider
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.source.ForecastSource
import app.sigot.forecast.data.source.QueryCostLogger

internal class VisualCrossingForecastSource(
    private val queryCostLogger: QueryCostLogger,
    private val api: VisualCrossingApi,
    private val nowProvider: NowProvider,
) : ForecastSource {
    override suspend fun forecastFor(location: Location): Forecast =
        makeRequest {
            api.forecastFor(location.latitude, location.longitude)
        }

    override suspend fun forecastFor(location: String): Forecast =
        makeRequest {
            api.forecastFor(location)
        }

    private suspend fun makeRequest(block: suspend () -> VCForecastResponse): Forecast {
        val response = block()
        queryCostLogger.log(response.queryCost)
        return response.toModel(nowProvider, 5) // TODO: Remove day and hour limit
    }
}
