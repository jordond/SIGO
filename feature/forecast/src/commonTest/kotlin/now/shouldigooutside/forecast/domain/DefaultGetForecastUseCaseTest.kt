package now.shouldigooutside.forecast.domain

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.convertTemperature
import now.shouldigooutside.test.FakeForecastRepo
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastBlock
import now.shouldigooutside.test.testForecastDay
import now.shouldigooutside.test.testLocation
import now.shouldigooutside.test.testTemperature
import kotlin.test.Test

class DefaultGetForecastUseCaseTest {
    @Test
    fun forecastFor_location_withoutUnits_returnsRepoResultUnchanged() =
        runTest {
            val location = testLocation()
            val forecast = testForecast(location = location)
            val repo = FakeForecastRepo(result = Result.success(forecast))
            val useCase = DefaultGetForecastUseCase(repo)

            val result = useCase.forecastFor(location, units = null)

            result.getOrThrow() shouldBe forecast
            repo.lastLocationObj shouldBe location
        }

    @Test
    fun forecastFor_location_withUnits_convertsSuccessfulForecast() =
        runTest {
            val kelvin = 300.0
            val forecast = testForecast(
                current = testForecastBlock(
                    temperature = testTemperature(
                        value = kelvin,
                        feelsLike = kelvin,
                        max = kelvin,
                        min = kelvin,
                    ),
                ),
                today = testForecastDay(),
                units = Units.SI,
            )
            val repo = FakeForecastRepo(result = Result.success(forecast))
            val useCase = DefaultGetForecastUseCase(repo)

            val result = useCase.forecastFor(testLocation(), units = Units.Metric)

            val converted = result.getOrThrow()
            converted.units shouldBe Units.Metric
            converted.current.temperature.value shouldBe (
                convertTemperature(kelvin, TemperatureUnit.Kelvin, TemperatureUnit.Celsius) plusOrMinus 0.01
            )
        }

    @Test
    fun forecastFor_string_withUnits_convertsSuccessfulForecast() =
        runTest {
            val kelvin = 290.0
            val forecast = testForecast(
                current = testForecastBlock(
                    temperature = testTemperature(
                        value = kelvin,
                        feelsLike = kelvin,
                        max = kelvin,
                        min = kelvin,
                    ),
                ),
                today = testForecastDay(),
                units = Units.SI,
            )
            val repo = FakeForecastRepo(result = Result.success(forecast))
            val useCase = DefaultGetForecastUseCase(repo)

            val result = useCase.forecastFor("Toronto", units = Units.Metric)

            result.getOrThrow().units shouldBe Units.Metric
            repo.lastLocationStr shouldBe "Toronto"
        }

    @Test
    fun forecastFor_failureResult_isReturnedWithoutConversion() =
        runTest {
            val failure = RuntimeException("network")
            val repo = FakeForecastRepo(result = Result.failure(failure))
            val useCase = DefaultGetForecastUseCase(repo)

            val result = useCase.forecastFor(testLocation(), units = Units.Metric)

            result.isFailure shouldBe true
            result.exceptionOrNull() shouldBe failure
        }
}
