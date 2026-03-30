package now.shouldigooutside.core.ui.mappers.units

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_temp_max
import now.shouldigooutside.core.resources.preferences_wind_max
import org.jetbrains.compose.resources.stringResource

@Composable
public fun Preferences.minTemperatureString(temperatureUnit: TemperatureUnit): String {
    val unit = temperatureUnit.rememberUnit()
    return remember(unit, minTemperature) {
        "$minTemperature$unit"
    }
}

@Composable
public fun Preferences.maxTemperatureString(temperatureUnit: TemperatureUnit): String {
    val unit = temperatureUnit.rememberUnit()
    return remember(unit, maxTemperature) {
        "$maxTemperature$unit"
    }
}

@Composable
public fun Preferences.maxWindSpeedString(windSpeedUnit: WindSpeedUnit): String {
    val unit = windSpeedUnit.rememberUnit()
    return remember(unit, windSpeed) {
        "$windSpeed $unit"
    }
}

@Composable
public fun Preferences.windSpeedString(unit: WindSpeedUnit): String {
    val value = maxWindSpeedString(unit)
    return stringResource(Res.string.preferences_wind_max, value)
}
