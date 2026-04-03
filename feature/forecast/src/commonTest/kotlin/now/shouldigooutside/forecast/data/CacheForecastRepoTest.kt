package now.shouldigooutside.forecast.data

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.forecast.data.source.FakeForecastCache
import now.shouldigooutside.test.FakeForecastRepo
import now.shouldigooutside.test.FakeNowProvider
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testLocation
import kotlin.test.Test
import kotlin.time.Instant

class CacheForecastRepoTest {
    @Test
    fun forecastFor_location_cacheHit_returnsCached() =
        runTest {
            val location = testLocation()
            val cached = testForecast(location = location, instant = Instant.fromEpochSeconds(100))
            val cache = FakeForecastCache(cached)
            val delegate = FakeForecastRepo()
            val nowProvider = FakeNowProvider(instant = Instant.fromEpochSeconds(500))
            val repo = CacheForecastRepo(cache, nowProvider, delegate)

            val result = repo.forecastFor(location)

            result.isSuccess shouldBe true
            delegate.lastLocationObj shouldBe null
        }

    @Test
    fun forecastFor_location_cacheHit_updatesInstant() =
        runTest {
            val location = testLocation()
            val cached = testForecast(location = location, instant = Instant.fromEpochSeconds(100))
            val cache = FakeForecastCache(cached)
            val nowProvider = FakeNowProvider(instant = Instant.fromEpochSeconds(999))
            val repo = CacheForecastRepo(cache, nowProvider, FakeForecastRepo())

            val result = repo.forecastFor(location)

            result.getOrThrow().instant shouldBe Instant.fromEpochSeconds(999)
        }

    @Test
    fun forecastFor_location_cacheMiss_delegatesToRepo() =
        runTest {
            val location = testLocation()
            val cache = FakeForecastCache(null)
            val delegate = FakeForecastRepo(result = Result.success(testForecast(location = location)))
            val repo = CacheForecastRepo(cache, FakeNowProvider(), delegate)

            repo.forecastFor(location)

            delegate.lastLocationObj shouldBe location
        }

    @Test
    fun forecastFor_differentLocation_delegatesInsteadOfReturningCached() =
        runTest {
            val cachedLocation = testLocation(name = "Toronto")
            val requestLocation = testLocation(name = "Vancouver", latitude = 49.2, longitude = -123.1)
            val cached = testForecast(location = cachedLocation)
            val cache = FakeForecastCache(cached)
            val delegate = FakeForecastRepo(result = Result.success(testForecast(location = requestLocation)))
            val repo = CacheForecastRepo(cache, FakeNowProvider(), delegate)

            repo.forecastFor(requestLocation)

            delegate.lastLocationObj shouldBe requestLocation
        }

    @Test
    fun forecastFor_location_force_bypassesCache() =
        runTest {
            val location = testLocation()
            val cached = testForecast(location = location)
            val cache = FakeForecastCache(cached)
            val delegate = FakeForecastRepo(result = Result.success(testForecast(location = location)))
            val repo = CacheForecastRepo(cache, FakeNowProvider(), delegate)

            repo.forecastFor(location, force = true)

            delegate.lastLocationObj shouldBe location
            delegate.lastForce shouldBe true
        }

    @Test
    fun forecastFor_location_delegateSuccess_savesToCache() =
        runTest {
            val location = testLocation()
            val fresh = testForecast(location = location)
            val cache = FakeForecastCache(null)
            val delegate = FakeForecastRepo(result = Result.success(fresh))
            val repo = CacheForecastRepo(cache, FakeNowProvider(), delegate)

            repo.forecastFor(location)

            cache.savedForecast shouldBe fresh
        }

    @Test
    fun forecastFor_string_cacheHit_matchesByName() =
        runTest {
            val locationName = "Toronto"
            val cached = testForecast(location = testLocation(name = locationName))
            val cache = FakeForecastCache(cached)
            val delegate = FakeForecastRepo()
            val repo = CacheForecastRepo(cache, FakeNowProvider(), delegate)

            val result = repo.forecastFor(locationName)

            result.isSuccess shouldBe true
            delegate.lastLocationStr shouldBe null
        }

    @Test
    fun forecastFor_string_cacheHit_doesNotUpdateInstant() =
        runTest {
            val locationName = "Toronto"
            val originalInstant = Instant.fromEpochSeconds(100)
            val cached = testForecast(
                location = testLocation(name = locationName),
                instant = originalInstant,
            )
            val cache = FakeForecastCache(cached)
            val nowProvider = FakeNowProvider(instant = Instant.fromEpochSeconds(999))
            val repo = CacheForecastRepo(cache, nowProvider, FakeForecastRepo())

            val result = repo.forecastFor(locationName)

            // String overload returns cached as-is without updating instant
            result.getOrThrow().instant shouldBe originalInstant
        }
}
