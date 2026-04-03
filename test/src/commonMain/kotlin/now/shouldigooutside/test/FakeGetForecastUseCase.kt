package now.shouldigooutside.test

import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.units.Units

public class FakeGetForecastUseCase(
    public var result: Result<Forecast> = Result.success(testForecast()),
) : GetForecastUseCase {
    public var lastLocationObj: Location? = null
    public var lastLocationStr: String? = null
    public var lastUnits: Units? = null

    override suspend fun forecastFor(
        location: Location,
        units: Units?,
    ): Result<Forecast> {
        lastLocationObj = location
        lastUnits = units
        return result
    }

    override suspend fun forecastFor(
        location: String,
        units: Units?,
    ): Result<Forecast> {
        lastLocationStr = location
        lastUnits = units
        return result
    }
}
