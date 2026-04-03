package now.shouldigooutside.forecast.data.source

import now.shouldigooutside.core.model.forecast.Forecast

class FakeForecastCache(
    private var cached: Forecast? = null,
) : ForecastCache {
    var savedForecast: Forecast? = null
    var clearCalled: Boolean = false

    override suspend fun get(): Forecast? = cached

    override suspend fun save(forecast: Forecast) {
        savedForecast = forecast
        cached = forecast
    }

    override suspend fun clear() {
        clearCalled = true
        cached = null
    }
}
