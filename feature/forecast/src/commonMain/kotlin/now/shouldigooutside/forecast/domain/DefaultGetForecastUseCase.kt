package now.shouldigooutside.forecast.domain

import now.shouldigooutside.core.domain.forecast.ForecastRepo
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.convert
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.units.Units

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
