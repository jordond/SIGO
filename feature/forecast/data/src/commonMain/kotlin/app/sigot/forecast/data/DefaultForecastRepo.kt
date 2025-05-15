package app.sigot.forecast.data

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.Location
import app.sigot.forecast.data.source.ForecastCache
import app.sigot.forecast.data.source.ForecastSource
import app.sigot.forecast.domain.ForecastRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

internal class DefaultForecastRepo(
    private val cache: ForecastCache,
    private val source: ForecastSource,
) : ForecastRepo {
    override suspend fun forecastFor(location: Location): Result<Forecast> {
        val cached = withContext(Dispatchers.Default) { cache.get() }
        if (cached != null && cached.location == location) {
            return Result.success(cached)
        }

        try {
            val result = withContext(Dispatchers.Default) {
                source.forecastFor(location).also { cache.save(it) }
            }
            return Result.success(result)
        } catch (cause: Throwable) {
            if (cause is CancellationException) throw cause
            return Result.failure(cause)
        }
    }
}
