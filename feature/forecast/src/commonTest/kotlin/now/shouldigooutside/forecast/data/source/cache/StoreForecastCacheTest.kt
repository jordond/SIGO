package now.shouldigooutside.forecast.data.source.cache

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.config.model.AppConfig
import now.shouldigooutside.forecast.data.entity.ForecastEntity
import now.shouldigooutside.forecast.data.entity.toEntity
import now.shouldigooutside.test.FakeAppConfigRepo
import now.shouldigooutside.test.FakeNowProvider
import now.shouldigooutside.test.NullableStore
import now.shouldigooutside.test.testForecast
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

class StoreForecastCacheTest {
    @Test
    fun get_whenStoreIsEmpty_returnsNull() =
        runTest {
            val store = NullableStore<ForecastEntity>()
            val cache = StoreForecastCache(store, FakeAppConfigRepo(), FakeNowProvider())

            cache.get() shouldBe null
        }

    @Test
    fun get_whenForecastIsFresh_returnsStoredForecast() =
        runTest {
            val forecast = testForecast(instant = Instant.fromEpochSeconds(0))
            val store = NullableStore(forecast.toEntity())
            val appConfigRepo = FakeAppConfigRepo(AppConfig(maxCacheAge = 15.minutes))
            val nowProvider = FakeNowProvider(instant = Instant.fromEpochSeconds(13 * 60L))
            val cache = StoreForecastCache(store, appConfigRepo, nowProvider)

            val result = cache.get()

            result shouldBe forecast
            store.clearCalled shouldBe false
        }

    @Test
    fun get_whenForecastIsExpired_clearsStoreAndReturnsNull() =
        runTest {
            val forecast = testForecast(instant = Instant.fromEpochSeconds(0))
            val store = NullableStore(forecast.toEntity())
            val appConfigRepo = FakeAppConfigRepo(AppConfig(maxCacheAge = 15.minutes))
            val nowProvider = FakeNowProvider(instant = Instant.fromEpochSeconds(15 * 60L))
            val cache = StoreForecastCache(store, appConfigRepo, nowProvider)

            val result = cache.get()

            result shouldBe null
            store.clearCalled shouldBe true
            store.get() shouldBe null
        }

    @Test
    fun save_persistsForecastEntity() =
        runTest {
            val store = NullableStore<ForecastEntity>()
            val forecast = testForecast()
            val cache = StoreForecastCache(store, FakeAppConfigRepo(), FakeNowProvider())

            cache.save(forecast)

            store.get() shouldBe forecast.toEntity()
        }

    @Test
    fun clear_removesStoredForecast() =
        runTest {
            val store = NullableStore(testForecast().toEntity())
            val cache = StoreForecastCache(store, FakeAppConfigRepo(), FakeNowProvider())

            cache.clear()

            store.clearCalled shouldBe true
            store.get() shouldBe null
        }
}
