package app.sigot.forecast.data

import app.sigot.forecast.domain.ForecastRepo
import app.sigot.forecast.domain.model.Forecast
import app.sigot.forecast.domain.model.Location

internal class DefaultForecastRepo : ForecastRepo {
    override suspend fun forecastFor(location: Location): Result<Forecast> {
        TODO("Not yet implemented")
    }
}
