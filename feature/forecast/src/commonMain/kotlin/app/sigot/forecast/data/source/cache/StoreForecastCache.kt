package app.sigot.forecast.data.source.cache

import app.sigot.core.foundation.NowProvider
import app.sigot.core.model.forecast.Forecast
import app.sigot.core.platform.store.Store
import app.sigot.forecast.data.source.ForecastCache
import app.sigot.forecast.data.source.cache.entity.ForecastCacheData
import app.sigot.forecast.data.source.cache.entity.toEntity
import app.sigot.forecast.data.source.cache.entity.toModel
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

internal class StoreForecastCache(
    private val store: Store<ForecastCacheData>,
    private val nowProvider: NowProvider,
) : ForecastCache {
    override suspend fun get(): Forecast? {
        val saved = store.get() ?: return null
        val elapsed = nowProvider.durationFromNow(Instant.fromEpochMilliseconds(saved.createdAt))
        if (elapsed > DEFAULT_EXPIRY_MINUTES) {
            return null
        }
        return saved.forecast.toModel()
    }

    override suspend fun save(forecast: Forecast) {
        val entity = forecast.toEntity()
        store.set(ForecastCacheData(entity, nowProvider.now().toEpochMilliseconds()))
    }

    override suspend fun clear() {
        store.clear()
    }

    private companion object {
        val DEFAULT_EXPIRY_MINUTES = 15.minutes
    }
}
