package now.shouldigooutside.core.domain.forecast

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location

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
