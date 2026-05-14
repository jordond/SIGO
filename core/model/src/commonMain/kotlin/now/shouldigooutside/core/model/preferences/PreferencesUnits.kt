package now.shouldigooutside.core.model.preferences

import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.model.units.convertTemperature
import now.shouldigooutside.core.model.units.convertWindSpeed

public fun Preferences.minTemperatureIn(target: TemperatureUnit): Double =
    convertTemperature(minTemperature, Units.Metric.temperature, target)

public fun Preferences.maxTemperatureIn(target: TemperatureUnit): Double =
    convertTemperature(maxTemperature, Units.Metric.temperature, target)

public fun Preferences.windSpeedIn(target: WindSpeedUnit): Double =
    convertWindSpeed(windSpeed, Units.Metric.windSpeed, target)

public fun Preferences.withMinTemperature(
    value: Double,
    from: TemperatureUnit,
): Preferences = copy(minTemperature = convertTemperature(value, from, Units.Metric.temperature))

public fun Preferences.withMaxTemperature(
    value: Double,
    from: TemperatureUnit,
): Preferences = copy(maxTemperature = convertTemperature(value, from, Units.Metric.temperature))

public fun Preferences.withWindSpeed(
    value: Double,
    from: WindSpeedUnit,
): Preferences = copy(windSpeed = convertWindSpeed(value, from, Units.Metric.windSpeed))
