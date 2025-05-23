package app.sigot.core.domain.forecast

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationResult
import app.sigot.core.model.units.Units

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

    /**
     * Get the forecast for the current location, then convert it to the given units.
     *
     * The location is requested from the geolocation provider, but if that fails it will throw
     * a [LocationResult.Failed] exception.
     *
     * @param units The units to convert the forecast to.
     * @return The forecast for the current location, then converted to the given units.
     */
    public suspend fun forecastForCurrentLocation(units: Units? = null): Result<Forecast>
}
