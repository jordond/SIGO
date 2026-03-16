package now.shouldigooutside.core.domain.forecast

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.units.Units

public interface GetForecastUseCase {
    /**
     * Get the forecast for the given location, then convert it to the given units.
     *
     * @param location The location to get the forecast for.
     * @param units The units to convert the forecast to.
     * @return The forecast for the given location, then converted to the given units.
     */
    public suspend fun forecastFor(
        location: Location,
        units: Units? = null,
    ): Result<Forecast>

    /**
     * Get the forecast for the given location name, then convert it to the given units.
     *
     * @param location The location name to get the forecast for.
     * @param units The units to convert the forecast to.
     * @return The forecast for the given location, then converted to the given units.
     */
    public suspend fun forecastFor(
        location: String,
        units: Units? = null,
    ): Result<Forecast>
}
