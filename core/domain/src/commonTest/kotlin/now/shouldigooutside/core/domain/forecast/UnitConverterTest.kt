package now.shouldigooutside.core.domain.forecast

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.forecast.PrecipitationType
import now.shouldigooutside.core.model.units.PrecipitationUnit
import now.shouldigooutside.core.model.units.PressureUnit
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.model.units.convertPrecipitation
import now.shouldigooutside.core.model.units.convertPressure
import now.shouldigooutside.core.model.units.convertTemperature
import now.shouldigooutside.core.model.units.convertWindSpeed
import now.shouldigooutside.test.testForecast
import now.shouldigooutside.test.testForecastBlock
import now.shouldigooutside.test.testForecastDay
import now.shouldigooutside.test.testPrecipitation
import now.shouldigooutside.test.testTemperature
import now.shouldigooutside.test.testWind
import kotlin.test.Test

private const val EPSILON = 1e-6

class UnitConverterTest {
    @Test
    fun sameUnitsSkipsConversion() {
        val forecast = testForecast(units = Units.SI)
        val converted = forecast.convert(Units.SI)
        converted shouldBe forecast
    }

    @Test
    fun convertsTemperature() {
        val kelvin = 300.0
        val expectedCelsius = convertTemperature(kelvin, TemperatureUnit.Kelvin, TemperatureUnit.Celsius)
        val block = testForecastBlock(
            temperature = testTemperature(value = kelvin, feelsLike = kelvin, max = kelvin, min = kelvin),
        )
        val forecast = testForecast(
            current = block,
            today = testForecastDay(block = block),
            units = Units.SI,
        )

        val converted = forecast.convert(Units.Metric)

        converted.current.temperature.value shouldBe (expectedCelsius plusOrMinus EPSILON)
        converted.current.temperature.feelsLike shouldBe (expectedCelsius plusOrMinus EPSILON)
        converted.current.temperature.max shouldBe (expectedCelsius plusOrMinus EPSILON)
        converted.current.temperature.min shouldBe (expectedCelsius plusOrMinus EPSILON)
    }

    @Test
    fun convertsWindSpeed() {
        val mps = 10.0
        val expectedKph = convertWindSpeed(mps, WindSpeedUnit.MeterPerSecond, WindSpeedUnit.KilometerPerHour)
        val block = testForecastBlock(
            wind = testWind(speed = mps, gust = mps, maxSpeed = mps, meanSpeed = mps, minSpeed = mps),
        )
        val forecast = testForecast(current = block, today = testForecastDay(block = block), units = Units.SI)

        val converted = forecast.convert(Units.Metric)

        converted.current.wind.speed shouldBe (expectedKph plusOrMinus EPSILON)
        converted.current.wind.gust shouldBe (expectedKph plusOrMinus EPSILON)
        converted.current.wind.maxSpeed shouldBe (expectedKph plusOrMinus EPSILON)
        converted.current.wind.meanSpeed shouldBe (expectedKph plusOrMinus EPSILON)
        converted.current.wind.minSpeed shouldBe (expectedKph plusOrMinus EPSILON)
    }

    @Test
    fun convertsPrecipitation() {
        val inches = 1.0
        val expectedMm = convertPrecipitation(
            inches,
            PrecipitationUnit.Inch,
            PrecipitationUnit.Millimeter,
        )
        val block = testForecastBlock(
            precipitation = testPrecipitation(
                amount = inches,
                probability = 50,
                types = setOf(PrecipitationType.Rain),
            ),
        )
        val forecast =
            testForecast(current = block, today = testForecastDay(block = block), units = Units.Imperial)

        val converted = forecast.convert(Units.Metric)

        converted.current.precipitation.amount shouldBe (expectedMm plusOrMinus EPSILON)
        converted.current.precipitation.probability shouldBe 50
        converted.current.precipitation.types shouldBe setOf(PrecipitationType.Rain)
    }

    @Test
    fun convertsPressure() {
        val inHg = 29.92
        val expectedHpa = convertPressure(inHg, PressureUnit.InchMercury, PressureUnit.HectoPascal)
        val block = testForecastBlock(pressure = inHg)
        val forecast =
            testForecast(current = block, today = testForecastDay(block = block), units = Units.Imperial)

        val converted = forecast.convert(Units.Metric)

        converted.current.pressure shouldBe (expectedHpa plusOrMinus EPSILON)
    }

    @Test
    fun preservesNonUnitFields() {
        val block = testForecastBlock(
            humidity = 65.0,
            cloudCoverPercent = 42,
            uvIndex = 5,
            visibility = 8.0,
        )
        val forecast = testForecast(current = block, today = testForecastDay(block = block), units = Units.SI)

        val converted = forecast.convert(Units.Metric)

        converted.current.humidity shouldBe 65.0
        converted.current.cloudCoverPercent shouldBe 42
        converted.current.uvIndex shouldBe 5
        converted.current.visibility shouldBe 8.0
    }

    @Test
    fun convertsAllHoursInToday() {
        val kelvin = 280.0
        val expectedCelsius = convertTemperature(kelvin, TemperatureUnit.Kelvin, TemperatureUnit.Celsius)
        val hourBlock = testForecastBlock(
            temperature = testTemperature(value = kelvin, feelsLike = kelvin, max = kelvin, min = kelvin),
        )
        val forecast = testForecast(
            today = testForecastDay(
                block = testForecastBlock(),
                hours = listOf(hourBlock, hourBlock, hourBlock),
            ),
            units = Units.SI,
        )

        val converted = forecast.convert(Units.Metric)

        converted.today.hours.size shouldBe 3
        converted.today.hours.forEach { hour ->
            hour.temperature.value shouldBe (expectedCelsius plusOrMinus EPSILON)
        }
    }

    @Test
    fun convertsAllDays() {
        val kelvin = 290.0
        val expectedCelsius = convertTemperature(kelvin, TemperatureUnit.Kelvin, TemperatureUnit.Celsius)
        val dayBlock = testForecastBlock(
            temperature = testTemperature(value = kelvin, feelsLike = kelvin, max = kelvin, min = kelvin),
        )
        val forecast = testForecast(
            days = listOf(
                testForecastDay(block = dayBlock),
                testForecastDay(block = dayBlock),
            ),
            units = Units.SI,
        )

        val converted = forecast.convert(Units.Metric)

        converted.days.size shouldBe 2
        converted.days.forEach { day ->
            day.block.temperature.value shouldBe (expectedCelsius plusOrMinus EPSILON)
        }
    }
}
