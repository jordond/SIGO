package app.sigot.forecast.data.source.visualcrossing

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.Location
import app.sigot.forecast.data.source.ForecastSource
import app.sigot.forecast.data.source.QueryCostLogger
import co.touchlab.kermit.Logger
import kotlin.coroutines.cancellation.CancellationException

internal class VisualCrossingForecastSource(
    private val queryCostLogger: QueryCostLogger,
    private val api: VisualCrossingApi,
) : ForecastSource {
    override suspend fun forecastFor(location: Location): Forecast =
        makeRequest(location.toString()) {
            api.forecastFor(location.latitude, location.longitude)
        }

    override suspend fun forecastFor(location: String): Forecast =
        makeRequest(location) {
            api.forecastFor(location)
        }

    private suspend fun makeRequest(
        location: String,
        block: suspend () -> VCForecastResponse,
    ): Forecast {
        try {
            val response = block()
            queryCostLogger.log(response.queryCost)
            return response.toModel()
        } catch (cause: Throwable) {
            if (cause is CancellationException) throw cause
            Logger.e(cause) { "Error fetching forecast for $location" }
            throw cause
        }
    }
}
