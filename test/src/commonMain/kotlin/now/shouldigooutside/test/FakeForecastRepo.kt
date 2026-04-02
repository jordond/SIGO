package now.shouldigooutside.test

import now.shouldigooutside.core.domain.forecast.ForecastRepo
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location

public class FakeForecastRepo(
    public var result: Result<Forecast> = Result.success(testForecast()),
) : ForecastRepo {
    public var lastLocationObj: Location? = null
    public var lastLocationStr: String? = null
    public var lastForce: Boolean? = null

    override suspend fun forecastFor(
        location: Location,
        force: Boolean,
    ): Result<Forecast> {
        lastLocationObj = location
        lastForce = force
        return result
    }

    override suspend fun forecastFor(
        location: String,
        force: Boolean,
    ): Result<Forecast> {
        lastLocationStr = location
        lastForce = force
        return result
    }
}
