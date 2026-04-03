package now.shouldigooutside.forecast.data

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.forecast.data.source.FakeForecastSource
import now.shouldigooutside.test.FakeIsSimulateFailureUseCase
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testLocation
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class DefaultForecastRepoTest {
    @Test
    fun forecastFor_location_delegatesToSource() =
        runTest {
            val source = FakeForecastSource()
            val repo = DefaultForecastRepo(source)
            val location = testLocation(latitude = 1.0, longitude = 2.0)

            repo.forecastFor(location)

            source.lastLocationObj shouldBe location
        }

    @Test
    fun forecastFor_location_copiesLocationOntoResult() =
        runTest {
            val sourceLocation = testLocation(latitude = 1.0, longitude = 2.0, name = "SourceName")
            val requestLocation = testLocation(latitude = 1.0, longitude = 2.0, name = "RequestName")
            val source = FakeForecastSource(result = testForecast(location = sourceLocation))
            val repo = DefaultForecastRepo(source)

            val result = repo.forecastFor(requestLocation)

            result.getOrThrow().location shouldBe requestLocation
        }

    @Test
    fun forecastFor_string_doesNotCopyLocation() =
        runTest {
            val sourceLocation = testLocation(name = "SourceName")
            val source = FakeForecastSource(result = testForecast(location = sourceLocation))
            val repo = DefaultForecastRepo(source)

            val result = repo.forecastFor("SomeQuery")

            result.getOrThrow().location shouldBe sourceLocation
        }

    @Test
    fun forecastFor_simulateFailureTrue_shortCircuits() =
        runTest {
            val source = FakeForecastSource()
            val simulate = FakeIsSimulateFailureUseCase(shouldFail = true)
            val repo = DefaultForecastRepo(source, simulate)

            val result = repo.forecastFor(testLocation())

            result.isFailure shouldBe true
            source.lastLocationObj shouldBe null
        }

    @Test
    fun forecastFor_nullSimulateFailure_proceeds() =
        runTest {
            val source = FakeForecastSource()
            val repo = DefaultForecastRepo(source, isSimulateFailure = null)
            val location = testLocation()

            val result = repo.forecastFor(location)

            result.isSuccess shouldBe true
            source.lastLocationObj shouldBe location
        }

    @Test
    fun forecastFor_exceptionFromSource_wrappedInResultFailure() =
        runTest {
            val source = FakeForecastSource()
            source.shouldThrow = RuntimeException("Network error")
            val repo = DefaultForecastRepo(source)

            val result = repo.forecastFor(testLocation())

            result.isFailure shouldBe true
            result.exceptionOrNull()?.message shouldBe "Network error"
        }

    @Test
    fun forecastFor_cancellationException_isRethrown() =
        runTest {
            val source = FakeForecastSource()
            source.shouldThrow = CancellationException("Cancelled")
            val repo = DefaultForecastRepo(source)

            assertFailsWith<CancellationException> {
                repo.forecastFor(testLocation())
            }
        }
}
