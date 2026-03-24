package now.shouldigooutside.core.ui.mappers.units

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.units.TemperatureUnit
import now.shouldigooutside.core.model.units.WindSpeedUnit
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_temp_max
import now.shouldigooutside.core.resources.preferences_temp_min
import now.shouldigooutside.core.resources.preferences_wind_max
import org.jetbrains.compose.resources.stringResource

@Composable
public fun Preferences.minTemperatureString(unit: TemperatureUnit): String {
    val unit = unit.rememberUnit()
    val value = remember(unit, minTemperature) {
        "$minTemperature$unit"
    }
    return stringResource(Res.string.preferences_temp_min, value)
}

@Composable
public fun Preferences.maxTemperatureString(unit: TemperatureUnit): String {
    val unit = unit.rememberUnit()
    val value = remember(unit, maxTemperature) {
        "$maxTemperature$unit"
    }
    return stringResource(Res.string.preferences_temp_max, value)
}

@Composable
public fun Preferences.windSpeedString(unit: WindSpeedUnit): String {
    val unit = unit.rememberUnit()
    val value = remember(unit, windSpeed) {
        "$windSpeed $unit"
    }
    return stringResource(Res.string.preferences_wind_max, value)
}
