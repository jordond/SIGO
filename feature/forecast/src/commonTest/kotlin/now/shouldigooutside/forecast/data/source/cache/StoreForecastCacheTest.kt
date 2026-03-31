package now.shouldigooutside.forecast.data.source.cache

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.config.model.AppConfig
import now.shouldigooutside.core.platform.store.Store
import now.shouldigooutside.forecast.data.entity.ForecastEntity
import now.shouldigooutside.forecast.data.entity.toEntity
import now.shouldigooutside.test.FakeAppConfigRepo
import now.shouldigooutside.test.FakeNowProvider
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

private class NullableStore<T : Any>(
    initial: T? = null,
) : Store<T> {
    private val state = MutableStateFlow(initial)
    var clearCalled: Boolean = false

    override val data: Flow<T> = state.filterNotNull()

    override suspend fun get(): T? = state.value

    override suspend fun set(data: T) {
        state.value = data
    }

    override suspend fun update(block: (T?) -> T) {
        state.update(block)
    }

    override suspend fun clear() {
        clearCalled = true
        state.value = null
    }
}
