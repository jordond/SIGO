package app.sigot.forecast.domain

import app.sigot.forecast.domain.model.Forecast
import app.sigot.forecast.domain.model.Location

public interface ForecastRepo {
    public suspend fun forecastFor(location: Location): Result<Forecast>
}
