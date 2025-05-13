package app.sigot.forecast.data.source.visualcrossing

import app.sigot.forecast.data.source.ForecastSource
import app.sigot.forecast.domain.model.Forecast
import app.sigot.forecast.domain.model.Location

internal class VisualCrossingForecastSource : ForecastSource {
    override suspend fun forecastFor(location: Location): Forecast {
        TODO("Not yet implemented")
    }
}
