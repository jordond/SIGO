package app.sigot.core.api.client

import app.sigot.core.model.Version
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location

public interface ApiClient {
    public suspend fun version(): ApiResult<Version>

    public suspend fun forecast(location: Location): ApiResult<Forecast>
}
