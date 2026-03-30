package now.shouldigooutside.core.domain.forecast

import io.kotest.matchers.shouldBe
import now.shouldigooutside.core.model.forecast.PrecipitationType
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
import kotlin.math.abs
import kotlin.test.Test

private const val EPSILON = 1e-6

private infix fun Double.shouldBeClose(expected: Double) {
    (abs(this - expected) < EPSILON) shouldBe true
}

class UnitConverterTest {
    @Test
    fun sameUnitsSkipsConversion() {
        val forecast = testForecast(units = Units.SI)
        val converted = forecast.convert(Units.SI)
        converted shouldBe forecast
    }

    @Test
    fun convertsTemperature() {
        // SI = Kelvin; Metric = Celsius; 300K -> 26.85°C
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

        converted.current.temperature.value shouldBeClose expectedCelsius
        converted.current.temperature.feelsLike shouldBeClose expectedCelsius
        converted.current.temperature.max shouldBeClose expectedCelsius
        converted.current.temperature.min shouldBeClose expectedCelsius
    }

    @Test
    fun convertsWindSpeed() {
        // SI windSpeed = MeterPerSecond; Metric = KilometerPerHour; 10 m/s -> 36 kph
        val mps = 10.0
        val expectedKph = convertWindSpeed(mps, WindSpeedUnit.MeterPerSecond, WindSpeedUnit.KilometerPerHour)
        val block = testForecastBlock(
            wind = testWind(speed = mps, gust = mps, maxSpeed = mps, meanSpeed = mps, minSpeed = mps),
        )
        val forecast = testForecast(current = block, today = testForecastDay(block = block), units = Units.SI)

        val converted = forecast.convert(Units.Metric)

        converted.current.wind.speed shouldBeClose expectedKph
        converted.current.wind.gust shouldBeClose expectedKph
        converted.current.wind.maxSpeed shouldBeClose expectedKph
        converted.current.wind.meanSpeed shouldBeClose expectedKph
        converted.current.wind.minSpeed shouldBeClose expectedKph
    }

    @Test
    fun convertsPrecipitation() {
        // Imperial precipitation = Inch; Metric = Millimeter; 1 inch -> 25.4 mm
        val inches = 1.0
        val expectedMm = convertPrecipitation(
            inches,
            now.shouldigooutside.core.model.units.PrecipitationUnit.Inch,
            now.shouldigooutside.core.model.units.PrecipitationUnit.Millimeter,
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

        converted.current.precipitation.amount shouldBeClose expectedMm
        // Non-unit fields preserved
        converted.current.precipitation.probability shouldBe 50
        converted.current.precipitation.types shouldBe setOf(PrecipitationType.Rain)
    }

    @Test
    fun convertsPressure() {
        // Imperial pressure = InchMercury; Metric = HectoPascal; 29.92 inHg -> ~1013.25 hPa
        val inHg = 29.92
        val expectedHpa = convertPressure(inHg, PressureUnit.InchMercury, PressureUnit.HectoPascal)
        val block = testForecastBlock(pressure = inHg)
        val forecast =
            testForecast(current = block, today = testForecastDay(block = block), units = Units.Imperial)

        val converted = forecast.convert(Units.Metric)

        converted.current.pressure shouldBeClose expectedHpa
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
            hour.temperature.value shouldBeClose expectedCelsius
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
            day.block.temperature.value shouldBeClose expectedCelsius
        }
    }
}
