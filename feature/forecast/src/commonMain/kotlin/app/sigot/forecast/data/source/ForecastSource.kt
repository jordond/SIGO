package app.sigot.forecast.data.source

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.Location

internal interface ForecastSource {
    suspend fun forecastFor(location: Location): Forecast

    suspend fun forecastFor(location: String): Forecast
}
