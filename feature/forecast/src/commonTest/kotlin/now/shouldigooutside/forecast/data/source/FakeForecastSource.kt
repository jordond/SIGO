package now.shouldigooutside.forecast.data.source

import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.test.testForecast

class FakeForecastSource(
    var result: Forecast = testForecast(),
) : ForecastSource {
    var lastLocationObj: Location? = null
    var lastLocationStr: String? = null
    var shouldThrow: Throwable? = null

    override suspend fun forecastFor(location: Location): Forecast {
        lastLocationObj = location
        shouldThrow?.let { throw it }
        return result
    }

    override suspend fun forecastFor(location: String): Forecast {
        lastLocationStr = location
        shouldThrow?.let { throw it }
        return result
    }
}
