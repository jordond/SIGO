package app.sigot.core.domain.forecast

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.Location

public interface ForecastRepo {
    public suspend fun forecastFor(
        location: Location,
        force: Boolean = false,
    ): Result<Forecast>

    public suspend fun forecastFor(
        location: String,
        force: Boolean = false,
    ): Result<Forecast>
}
