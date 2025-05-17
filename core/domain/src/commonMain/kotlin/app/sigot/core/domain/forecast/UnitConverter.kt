package app.sigot.core.domain.forecast

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.forecast.Precipitation
import app.sigot.core.model.forecast.Temperature
import app.sigot.core.model.forecast.Wind
import app.sigot.core.model.units.PrecipitationUnit
import app.sigot.core.model.units.PressureUnit
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.model.units.Units
import app.sigot.core.model.units.WindSpeedUnit

private const val KELVIN_TO_CELSIUS = 273.15
private const val MILLIMETER_TO_INCH = 25.4
private const val MPS_TO_KPH = 3.6
private const val MPS_TO_MPH = 2.23694
private const val KPH_TO_MPH = 0.621371
private const val MPH_TO_KPH = 1.60934
private const val HPA_TO_INCH_MERCURY = 0.02953

public fun Forecast.convert(units: Units): Forecast {
    if (this.units == units) return this
    return copy(
        units = units,
        current = current.convert(this.units, units),
        daily = daily.map { day ->
            day.copy(
                block = day.block.convert(this.units, units),
                hours = day.hours.map { it.convert(this.units, units) },
            )
        },
    )
}

private fun ForecastBlock.convert(
    current: Units,
    target: Units,
): ForecastBlock =
    copy(
        temperature = temperature.convert(current.temperature, target.temperature),
        wind = wind.convert(current.windSpeed, target.windSpeed),
        precipitation = precipitation.convert(current.precipitation, target.precipitation),
        pressure = convertPressure(pressure, current.pressure, target.pressure),
    )

internal fun Temperature.convert(
    from: TemperatureUnit,
    target: TemperatureUnit,
): Temperature =
    copy(
        value = convertTemperature(value, from, target),
        feelsLike = convertTemperature(feelsLike, from, target),
        min = convertTemperature(min, from, target),
        max = convertTemperature(max, from, target),
    )

internal fun Precipitation.convert(
    from: PrecipitationUnit,
    target: PrecipitationUnit,
): Precipitation = copy(amount = convertPrecipitation(amount, from, target))

internal fun Wind.convert(
    from: WindSpeedUnit,
    target: WindSpeedUnit,
): Wind =
    copy(
        speed = convertWindSpeed(speed, from, target),
        gust = convertWindSpeed(gust, from, target),
        maxSpeed = convertWindSpeed(maxSpeed, from, target),
        meanSpeed = convertWindSpeed(meanSpeed, from, target),
        minSpeed = convertWindSpeed(minSpeed, from, target),
    )

internal fun convertTemperature(
    value: Double,
    from: TemperatureUnit,
    target: TemperatureUnit,
): Double =
    when (from) {
        TemperatureUnit.Kelvin -> when (target) {
            TemperatureUnit.Kelvin -> value
            TemperatureUnit.Celsius -> value - KELVIN_TO_CELSIUS
            TemperatureUnit.Fahrenheit -> (value - KELVIN_TO_CELSIUS) * 9 / 5 + 32
        }
        TemperatureUnit.Celsius -> when (target) {
            TemperatureUnit.Kelvin -> value + KELVIN_TO_CELSIUS
            TemperatureUnit.Celsius -> value
            TemperatureUnit.Fahrenheit -> value * 9 / 5 + 32
        }
        TemperatureUnit.Fahrenheit -> when (target) {
            TemperatureUnit.Kelvin -> (value - 32) * 5 / 9 + KELVIN_TO_CELSIUS
            TemperatureUnit.Celsius -> (value - 32) * 5 / 9
            TemperatureUnit.Fahrenheit -> value
        }
    }

internal fun convertPrecipitation(
    value: Double,
    from: PrecipitationUnit,
    target: PrecipitationUnit,
): Double =
    when (from) {
        PrecipitationUnit.Millimeter -> when (target) {
            PrecipitationUnit.Millimeter -> value
            PrecipitationUnit.Inch -> value / MILLIMETER_TO_INCH
        }
        PrecipitationUnit.Inch -> when (target) {
            PrecipitationUnit.Millimeter -> value * MILLIMETER_TO_INCH
            PrecipitationUnit.Inch -> value
        }
    }

internal fun convertWindSpeed(
    value: Double,
    from: WindSpeedUnit,
    target: WindSpeedUnit,
): Double =
    when (from) {
        WindSpeedUnit.MeterPerSecond -> when (target) {
            WindSpeedUnit.MeterPerSecond -> value
            WindSpeedUnit.KilometerPerHour -> value * MPS_TO_KPH
            WindSpeedUnit.MilePerHour -> value * MPS_TO_MPH
        }
        WindSpeedUnit.KilometerPerHour -> when (target) {
            WindSpeedUnit.MeterPerSecond -> value / MPS_TO_KPH
            WindSpeedUnit.KilometerPerHour -> value
            WindSpeedUnit.MilePerHour -> value * KPH_TO_MPH
        }
        WindSpeedUnit.MilePerHour -> when (target) {
            WindSpeedUnit.MeterPerSecond -> value / MPS_TO_MPH
            WindSpeedUnit.KilometerPerHour -> value * MPH_TO_KPH
            WindSpeedUnit.MilePerHour -> value
        }
    }

internal fun convertPressure(
    value: Double,
    from: PressureUnit,
    target: PressureUnit,
): Double =
    when (from) {
        PressureUnit.HectoPascal -> when (target) {
            PressureUnit.HectoPascal -> value
            PressureUnit.InchMercury -> value * HPA_TO_INCH_MERCURY
        }
        PressureUnit.InchMercury -> when (target) {
            PressureUnit.HectoPascal -> value / HPA_TO_INCH_MERCURY
            PressureUnit.InchMercury -> value
        }
    }
