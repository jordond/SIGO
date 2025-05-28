package app.sigot.forecast.data

import app.sigot.core.domain.forecast.ForecastRepo
import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.foundation.NowProvider
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.location.Location
import app.sigot.forecast.data.source.ForecastCache
import app.sigot.forecast.data.source.ForecastSource
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

internal class DefaultForecastRepo(
    private val cache: ForecastCache,
    private val source: ForecastSource,
    private val nowProvider: NowProvider,
    private val settingsRepo: SettingsRepo,
) : ForecastRepo {
    private val logger = Logger.withTag("DefaultForecastRepo")

    override suspend fun forecastFor(
        location: Location,
        force: Boolean,
    ): Result<Forecast> {
        if (settingsRepo.settings.value.internalSettings.simulateFailure) {
            return Result.failure(RuntimeException("Simulated failure"))
        }

        if (!force) {
            logger.d { "Checking cache for location=$location" }
            val cached = withContext(Dispatchers.Default) { cache.get() }
            if (cached != null && cached.location == location) {
                logger.d { "Cache hit for location=$location" }
                return Result.success(cached.copy(instant = nowProvider.now()))
            }
        }

        logger.d { "Fetching fresh forecast for location=$location" }
        return runCatching {
            withContext(Dispatchers.Default) {
                source.forecastFor(location).also { cache.save(it) }
            }
        }.onFailure { cause ->
            if (cause is CancellationException) throw cause
        }
    }

    override suspend fun forecastFor(
        location: String,
        force: Boolean,
    ): Result<Forecast> {
        if (settingsRepo.settings.value.internalSettings.simulateFailure) {
            return Result.failure(RuntimeException("Simulated failure"))
        }

        if (!force) {
            logger.d { "Checking cache for location=$location" }
            val cached = withContext(Dispatchers.Default) { cache.get() }
            if (cached != null && cached.location.name == location) {
                logger.d { "Cache hit for location=$location" }
                return Result.success(cached)
            }
        }

        logger.d { "Fetching fresh forecast for location=$location" }
        return runCatching {
            withContext(Dispatchers.Default) {
                source.forecastFor(location).also { cache.save(it) }
            }
        }.onFailure { cause ->
            if (cause is CancellationException) throw cause
        }
    }
}
