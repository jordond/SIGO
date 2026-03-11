package app.sigot.forecast.domain

import app.sigot.core.domain.forecast.ClearForecastUseCase
import app.sigot.forecast.data.source.ForecastCache

internal class DefaultClearForecastUseCase(
    private val cache: ForecastCache,
) : ClearForecastUseCase {
    override suspend fun clear() {
        cache.clear()
    }
}
