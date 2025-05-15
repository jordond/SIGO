package app.sigot.forecast.domain

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.Location

public interface ForecastRepo {
    public suspend fun forecastFor(location: Location): Result<Forecast>
}
