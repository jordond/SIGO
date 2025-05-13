package app.sigot.forecast.data.source

import app.sigot.forecast.domain.model.Forecast
import app.sigot.forecast.domain.model.Location

internal interface ForecastSource {
    suspend fun forecastFor(location: Location): Forecast
}
