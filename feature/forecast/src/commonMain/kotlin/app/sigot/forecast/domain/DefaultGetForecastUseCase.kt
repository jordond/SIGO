package app.sigot.forecast.domain

import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.domain.forecast.GetForecastUseCase
import app.sigot.core.domain.forecast.convert
import app.sigot.core.domain.location.LocationRepo
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationResult
import app.sigot.core.model.units.Units

internal class DefaultGetForecastUseCase(
    private val locationRepo: LocationRepo,
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

    /**
     * @see GetForecastUseCase.forecastForCurrentLocation
     */
    override suspend fun forecastForCurrentLocation(units: Units?): Result<Forecast> {
        val result = locationRepo.location()
        return when (result) {
            is LocationResult.Failed -> Result.failure(result)
            is LocationResult.Success -> forecastFor(result.location, units)
        }
    }
}

private fun Result<Forecast>.convertOrNot(units: Units?): Result<Forecast> {
    if (units == null) return this
    return map { forecast -> forecast.convert(units) }
}
