package now.shouldigooutside.forecast.data.source.cache

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import now.shouldigooutside.core.config.AppConfigRepo
import now.shouldigooutside.core.foundation.NowProvider
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.platform.store.Store
import now.shouldigooutside.forecast.data.entity.ForecastEntity
import now.shouldigooutside.forecast.data.entity.toEntity
import now.shouldigooutside.forecast.data.entity.toModel
import now.shouldigooutside.forecast.data.source.ForecastCache
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
        withContext(Dispatchers.Default) {
            store.clear()
        }
    }
}
