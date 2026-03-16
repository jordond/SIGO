package now.shouldigooutside.forecast.data.source

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location

public interface ForecastSource {
    public suspend fun forecastFor(location: Location): Forecast

    public suspend fun forecastFor(location: String): Forecast
}
