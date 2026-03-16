package now.shouldigooutside.forecast.data.source

import now.shouldigooutside.core.model.Clearable
import now.shouldigooutside.core.model.forecast.Forecast

internal interface ForecastCache : Clearable {
    suspend fun get(): Forecast?

    suspend fun save(forecast: Forecast)
}
