package app.sigot.forecast.data.source

import app.sigot.core.model.Clearable
import app.sigot.core.model.forecast.Forecast

internal interface ForecastCache : Clearable {
    suspend fun get(): Forecast?

    suspend fun save(forecast: Forecast)
}
