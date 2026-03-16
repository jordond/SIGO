package now.shouldigooutside.forecast.domain

import now.shouldigooutside.core.domain.forecast.ClearForecastUseCase
import now.shouldigooutside.forecast.data.source.ForecastCache

internal class DefaultClearForecastUseCase(
    private val cache: ForecastCache,
) : ClearForecastUseCase {
    override suspend fun clear() {
        cache.clear()
    }
}
