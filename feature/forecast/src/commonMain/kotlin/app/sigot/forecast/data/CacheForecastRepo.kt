package app.sigot.forecast.data

import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.foundation.NowProvider
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.source.ForecastCache
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class CacheForecastRepo(
    private val cache: ForecastCache,
    private val nowProvider: NowProvider,
    private val delegate: ForecastRepo,
) : ForecastRepo {
    private val logger = Logger.withTag("CacheForecastRepo")

    override suspend fun forecastFor(
        location: Location,
        force: Boolean,
    ): Result<Forecast> {
        if (!force) {
            logger.d { "Checking cache for location=$location" }
            val cached = withContext(Dispatchers.Default) { cache.get() }
            if (cached != null && cached.location == location) {
                logger.d { "Cache hit for location=$location" }
                return Result.success(cached.copy(instant = nowProvider.now()))
            }
        }

        return delegate.forecastFor(location, force).onSuccess { cache.save(it) }
    }

    override suspend fun forecastFor(
        location: String,
        force: Boolean,
    ): Result<Forecast> {
        if (!force) {
            logger.d { "Checking cache for location=$location" }
            val cached = withContext(Dispatchers.Default) { cache.get() }
            if (cached != null && cached.location.name == location) {
                logger.d { "Cache hit for location=$location" }
                return Result.success(cached)
            }
        }

        return delegate.forecastFor(location, force).onSuccess { cache.save(it) }
    }
}
