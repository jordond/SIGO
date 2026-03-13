package app.sigot.core.ui.mappers.units

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.sigot.core.model.preferences.Preferences
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_temp_max
import now.shouldigooutside.core.resources.preferences_temp_min
import now.shouldigooutside.core.resources.preferences_wind_max
import org.jetbrains.compose.resources.stringResource

@Composable
public fun Preferences.minTemperatureString(): String {
    val unit = units.temperature.rememberUnit()
    val value = remember(unit, minTemperature) {
        "$minTemperature$unit"
    }
    return stringResource(Res.string.preferences_temp_min, value)
}

@Composable
public fun Preferences.maxTemperatureString(): String {
    val unit = units.temperature.rememberUnit()
    val value = remember(unit, maxTemperature) {
        "$maxTemperature$unit"
    }
    return stringResource(Res.string.preferences_temp_max, value)
}

@Composable
public fun Preferences.windSpeedString(): String {
    val unit = units.windSpeed.rememberUnit()
    val value = remember(unit, windSpeed) {
        "$windSpeed $unit"
    }
    return stringResource(Res.string.preferences_wind_max, value)
}
