package app.sigot.core.domain.forecast

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.core.model.units.Units
import kotlinx.coroutines.flow.Flow

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

    public suspend fun forecastForCurrentLocation(units: Units? = null): Result<Forecast>

    /**
     * Get the forecast for the current location.
     *
     * This will use a ticker to update the forecast for the configured interval. It will also convert
     * the forecast to the configured units.
     *
     * @return A stream of the forecast for the current location in the configured units.
     */
    public fun forecast(): Flow<Result<Forecast>>
}
