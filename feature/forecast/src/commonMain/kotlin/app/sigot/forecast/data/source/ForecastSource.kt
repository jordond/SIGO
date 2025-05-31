package app.sigot.forecast.data.source

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location

public interface ForecastSource {
    public suspend fun forecastFor(location: Location): Forecast

    public suspend fun forecastFor(location: String): Forecast
}
