package app.sigot.forecast.data.source.cache

import app.sigot.core.config.AppConfigRepo
import app.sigot.core.foundation.NowProvider
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.platform.store.Store
import app.sigot.forecast.data.entity.ForecastEntity
import app.sigot.forecast.data.entity.toEntity
import app.sigot.forecast.data.entity.toModel
import app.sigot.forecast.data.source.ForecastCache
import co.touchlab.kermit.Logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

internal class StoreForecastCache(
    private val store: Store<ForecastEntity>,
    private val appConfigRepo: AppConfigRepo,
    private val nowProvider: NowProvider,
) : ForecastCache {
    private val logger = Logger.withTag("StoreForecastCache")
    private val expiry: Duration get() = appConfigRepo.value.maxCacheAge - 1.minutes

    override suspend fun get(): Forecast? {
        val saved = store.get()
        if (saved == null) {
            logger.d { "Cache is empty" }
            return null
        }

        val elapsed = nowProvider.durationFromNow(Instant.fromEpochMilliseconds(saved.updatedAt))
        logger.d {
            "Cache is not empty, last updated ${elapsed.inWholeSeconds}s ago, " +
                "max is ${expiry.inWholeSeconds}s"
        }
        if (elapsed > expiry) {
            logger.d { "Cache is expired, returning null" }
            clear()
            return null
        }
        return saved.toModel()
    }

    override suspend fun save(forecast: Forecast) {
        val entity = forecast.toEntity()
        store.set(entity)
    }

    override suspend fun clear() {
        store.clear()
    }
}
