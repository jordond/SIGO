package now.shouldigooutside.core.model.units

import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ConvertTest {
    @Test
    fun celsiusToFahrenheit_freezingPoint_returns32() {
        convertTemperature(0.0, TemperatureUnit.Celsius, TemperatureUnit.Fahrenheit) shouldBe
            (32.0 plusOrMinus 0.01)
    }

    @Test
    fun fahrenheitToCelsius_boilingPoint_returns100() {
        convertTemperature(212.0, TemperatureUnit.Fahrenheit, TemperatureUnit.Celsius) shouldBe
            (100.0 plusOrMinus 0.01)
    }

    @Test
    fun kelvinToCelsius_absoluteZeroOffset_returnsZero() {
        convertTemperature(273.15, TemperatureUnit.Kelvin, TemperatureUnit.Celsius) shouldBe
            (0.0 plusOrMinus 0.01)
    }

    @Test
    fun celsiusToKelvin_freezingPoint_returns273_15() {
        convertTemperature(0.0, TemperatureUnit.Celsius, TemperatureUnit.Kelvin) shouldBe
            (273.15 plusOrMinus 0.01)
    }

    @Test
    fun kelvinToFahrenheit_273_15K_returns32() {
        convertTemperature(273.15, TemperatureUnit.Kelvin, TemperatureUnit.Fahrenheit) shouldBe
            (32.0 plusOrMinus 0.01)
    }

    @Test
    fun fahrenheitToKelvin_32F_returns273_15() {
        convertTemperature(32.0, TemperatureUnit.Fahrenheit, TemperatureUnit.Kelvin) shouldBe
            (273.15 plusOrMinus 0.01)
    }

    @Test
    fun celsiusToCelsius_identity_returnsSameValue() {
        convertTemperature(20.0, TemperatureUnit.Celsius, TemperatureUnit.Celsius) shouldBe
            (20.0 plusOrMinus 0.01)
    }

    @Test
    fun fahrenheitToFahrenheit_identity_returnsSameValue() {
        convertTemperature(98.6, TemperatureUnit.Fahrenheit, TemperatureUnit.Fahrenheit) shouldBe
            (98.6 plusOrMinus 0.01)
    }

    @Test
    fun kelvinToKelvin_identity_returnsSameValue() {
        convertTemperature(300.0, TemperatureUnit.Kelvin, TemperatureUnit.Kelvin) shouldBe
            (300.0 plusOrMinus 0.01)
    }

    @Test
    fun celsiusToFahrenheit_negative40_returnsNegative40() {
        convertTemperature(-40.0, TemperatureUnit.Celsius, TemperatureUnit.Fahrenheit) shouldBe
            (-40.0 plusOrMinus 0.01)
    }

    @Test
    fun kelvinToCelsius_absoluteZero_returnsNegative273_15() {
        convertTemperature(0.0, TemperatureUnit.Kelvin, TemperatureUnit.Celsius) shouldBe
            (-273.15 plusOrMinus 0.01)
    }

    @Test
    fun mpsToKph_1mps_returns3_6() {
        convertWindSpeed(1.0, WindSpeedUnit.MeterPerSecond, WindSpeedUnit.KilometerPerHour) shouldBe
            (3.6 plusOrMinus 0.01)
    }

    @Test
    fun mpsToMph_1mps_returns2_237() {
        convertWindSpeed(1.0, WindSpeedUnit.MeterPerSecond, WindSpeedUnit.MilePerHour) shouldBe
            (2.237 plusOrMinus 0.01)
    }

    @Test
    fun kphToMph_100kph_returns62_14() {
        convertWindSpeed(100.0, WindSpeedUnit.KilometerPerHour, WindSpeedUnit.MilePerHour) shouldBe
            (62.14 plusOrMinus 0.01)
    }

    @Test
    fun mphToKph_60mph_returns96_56() {
        convertWindSpeed(60.0, WindSpeedUnit.MilePerHour, WindSpeedUnit.KilometerPerHour) shouldBe
            (96.56 plusOrMinus 0.01)
    }

    @Test
    fun mpsToMps_identity_returnsSameValue() {
        convertWindSpeed(10.0, WindSpeedUnit.MeterPerSecond, WindSpeedUnit.MeterPerSecond) shouldBe
            (10.0 plusOrMinus 0.01)
    }

    @Test
    fun windSpeed_zero_returnsZero() {
        convertWindSpeed(0.0, WindSpeedUnit.MeterPerSecond, WindSpeedUnit.KilometerPerHour) shouldBe
            (0.0 plusOrMinus 0.01)
    }

    @Test
    fun mmToInch_25_4mm_returns1() {
        convertPrecipitation(25.4, PrecipitationUnit.Millimeter, PrecipitationUnit.Inch) shouldBe
            (1.0 plusOrMinus 0.001)
    }

    @Test
    fun inchToMm_1inch_returns25_4() {
        convertPrecipitation(1.0, PrecipitationUnit.Inch, PrecipitationUnit.Millimeter) shouldBe
            (25.4 plusOrMinus 0.01)
    }

    @Test
    fun mmToMm_identity_returnsSameValue() {
        convertPrecipitation(10.0, PrecipitationUnit.Millimeter, PrecipitationUnit.Millimeter) shouldBe
            (10.0 plusOrMinus 0.001)
    }

    @Test
    fun hpaToInHg_1013_25hpa_returns29_92() {
        convertPressure(1013.25, PressureUnit.HectoPascal, PressureUnit.InchMercury) shouldBe
            (29.92 plusOrMinus 0.01)
    }

    @Test
    fun inHgToHpa_29_92inHg_returns1013_25() {
        convertPressure(29.92, PressureUnit.InchMercury, PressureUnit.HectoPascal) shouldBe
            (1013.25 plusOrMinus 1.0)
    }

    @Test
    fun hpaToHpa_identity_returnsSameValue() {
        convertPressure(1000.0, PressureUnit.HectoPascal, PressureUnit.HectoPascal) shouldBe
            (1000.0 plusOrMinus 0.01)
    }
}
