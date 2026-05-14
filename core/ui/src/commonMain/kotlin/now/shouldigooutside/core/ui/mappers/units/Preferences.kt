package now.shouldigooutside.core.ui.mappers.units

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.preferences.maxTemperatureIn
import now.shouldigooutside.core.model.preferences.minTemperatureIn
import now.shouldigooutside.core.model.preferences.windSpeedIn
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_temp_max
import now.shouldigooutside.core.resources.preferences_wind_max
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
public fun Preferences.minTemperatureString(temperatureUnit: TemperatureUnit): String {
    val unit = temperatureUnit.rememberUnit()
    return remember(unit, minTemperature, temperatureUnit) {
        "${minTemperatureIn(temperatureUnit).roundToInt()}$unit"
    }
}

@Composable
public fun Preferences.maxTemperatureString(temperatureUnit: TemperatureUnit): String {
    val unit = temperatureUnit.rememberUnit()
    return remember(unit, maxTemperature, temperatureUnit) {
        "${maxTemperatureIn(temperatureUnit).roundToInt()}$unit"
    }
}

@Composable
public fun Preferences.maxWindSpeedString(windSpeedUnit: WindSpeedUnit): String {
    val unit = windSpeedUnit.rememberUnit()
    return remember(unit, windSpeed, windSpeedUnit) {
        "${windSpeedIn(windSpeedUnit).roundToInt()} $unit"
    }
}

@Composable
public fun Preferences.windSpeedString(unit: WindSpeedUnit): String {
    val value = maxWindSpeedString(unit)
    return stringResource(Res.string.preferences_wind_max, value)
}
