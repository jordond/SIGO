package now.shouldigooutside.core.api.client

import now.shouldigooutside.core.model.Version
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location

public interface ApiClient {
    public suspend fun version(): Version

    public suspend fun forecast(location: Location): Forecast
}
