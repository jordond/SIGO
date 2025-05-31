package app.sigot.forecast.domain

import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.convert
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.core.model.units.Units

internal class DefaultGetForecastUseCase(
    private val forecastRepo: ForecastRepo,
) : GetForecastUseCase {
    /**
     * @see GetForecastUseCase.forecastFor
     */
    override suspend fun forecastFor(
        location: Location,
        units: Units?,
    ): Result<Forecast> = forecastRepo.forecastFor(location).convertOrNot(units)

    /**
     * @see GetForecastUseCase.forecastFor
     */
    override suspend fun forecastFor(
        location: String,
        units: Units?,
    ): Result<Forecast> = forecastRepo.forecastFor(location).convertOrNot(units)
}

private fun Result<Forecast>.convertOrNot(units: Units?): Result<Forecast> {
    if (units == null) return this
    return map { forecast -> forecast.convert(units) }
}
