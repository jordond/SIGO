package app.sigot.forecast.data.source.cache

import app.sigot.core.config.AppConfigRepo
import app.sigot.core.foundation.NowProvider
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.platform.store.Store
import app.sigot.forecast.data.source.ForecastCache
import app.sigot.forecast.data.source.cache.entity.ForecastEntity
import app.sigot.forecast.data.source.cache.entity.toEntity
import app.sigot.forecast.data.source.cache.entity.toModel
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

internal class StoreForecastCache(
    private val store: Store<ForecastEntity>,
    private val appConfigRepo: AppConfigRepo,
    private val nowProvider: NowProvider,
) : ForecastCache {
    private val expiry: Duration get() = appConfigRepo.value.maxCacheAge - 1.minutes

    override suspend fun get(): Forecast? {
        val saved = store.get() ?: return null
        val elapsed = nowProvider.durationFromNow(Instant.fromEpochMilliseconds(saved.updatedAt))
        if (elapsed > expiry) {
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
