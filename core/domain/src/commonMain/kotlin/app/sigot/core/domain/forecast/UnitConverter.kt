package app.sigot.core.domain.forecast

import app.sigot.core.model.forecast.Forecast
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.forecast.Precipitation
import app.sigot.core.model.forecast.Temperature
import app.sigot.core.model.forecast.Wind
import app.sigot.core.model.units.PrecipitationUnit
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.model.units.Units
import app.sigot.core.model.units.WindSpeedUnit
import app.sigot.core.model.units.convertPrecipitation
import app.sigot.core.model.units.convertPressure
import app.sigot.core.model.units.convertTemperature
import app.sigot.core.model.units.convertWindSpeed

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
