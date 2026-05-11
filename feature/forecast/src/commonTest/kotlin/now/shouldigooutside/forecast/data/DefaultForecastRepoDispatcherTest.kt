package now.shouldigooutside.forecast.data

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.forecast.Forecast
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.forecast.data.source.ForecastSource
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testLocation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test

class DefaultForecastRepoDispatcherTest {
    @Test
    fun forecastFor_location_runsSourceOnCallerDispatcher() =
        runTest {
            val callerDispatcher = currentCoroutineContext()[ContinuationInterceptor]
            var sourceDispatcher: ContinuationInterceptor? = null

            val source = object : ForecastSource {
                override suspend fun forecastFor(location: Location): Forecast {
                    sourceDispatcher = currentCoroutineContext()[ContinuationInterceptor]
                    return testForecast()
                }

                override suspend fun forecastFor(location: String): Forecast = testForecast()
            }

            DefaultForecastRepo(source).forecastFor(testLocation())

            sourceDispatcher.shouldNotBeNull()
            sourceDispatcher shouldBe callerDispatcher
        }

    @Test
    fun forecastFor_string_runsSourceOnCallerDispatcher() =
        runTest {
            val callerDispatcher = currentCoroutineContext()[ContinuationInterceptor]
            var sourceDispatcher: ContinuationInterceptor? = null

            val source = object : ForecastSource {
                override suspend fun forecastFor(location: Location): Forecast = testForecast()

                override suspend fun forecastFor(location: String): Forecast {
                    sourceDispatcher = currentCoroutineContext()[ContinuationInterceptor]
                    return testForecast()
                }
            }

            DefaultForecastRepo(source).forecastFor("Toronto")

            sourceDispatcher.shouldNotBeNull()
            sourceDispatcher shouldBe callerDispatcher
        }
}
