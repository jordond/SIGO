package app.sigot.core.ui.units

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import app.sigot.core.model.units.BaseUnit
import app.sigot.core.model.units.PrecipitationUnit
import app.sigot.core.model.units.PressureUnit
import app.sigot.core.model.units.TemperatureUnit
import app.sigot.core.model.units.WindSpeedUnit
import app.sigot.core.resources.Res
import app.sigot.core.resources.unit_precipitation
import app.sigot.core.resources.unit_precipitation_inch
import app.sigot.core.resources.unit_precipitation_mm
import app.sigot.core.resources.unit_pressure
import app.sigot.core.resources.unit_pressure_hecto_pascal
import app.sigot.core.resources.unit_pressure_inch_mercury
import app.sigot.core.resources.unit_temperature
import app.sigot.core.resources.unit_temperature_celsius
import app.sigot.core.resources.unit_temperature_fahrenheit
import app.sigot.core.resources.unit_temperature_kelvin
import app.sigot.core.resources.unit_wind
import app.sigot.core.resources.unit_wind_kph
import app.sigot.core.resources.unit_wind_mph
import app.sigot.core.resources.unit_wind_ms
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.BrutalColors
import app.sigot.core.ui.brutal
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.CloudRain
import app.sigot.core.ui.icons.lucide.Thermometer
import app.sigot.core.ui.icons.lucide.Waves
import app.sigot.core.ui.icons.lucide.Wind
import app.sigot.core.ui.ktx.get
import org.jetbrains.compose.resources.StringResource

public fun BaseUnit.titleResource(): StringResource =
    when (this) {
        is PrecipitationUnit -> Res.string.unit_precipitation
        is PressureUnit -> Res.string.unit_pressure
        is TemperatureUnit -> Res.string.unit_temperature
        is WindSpeedUnit -> Res.string.unit_wind
    }

@Composable
public fun BaseUnit.rememberTitle(): String {
    val res = remember(this) { titleResource() }
    return res.get()
}

public fun BaseUnit.labelResource(): StringResource =
    when (this) {
        is PrecipitationUnit -> when (this) {
            PrecipitationUnit.Millimeter -> Res.string.unit_precipitation_mm
            PrecipitationUnit.Inch -> Res.string.unit_precipitation_inch
        }
        is PressureUnit -> when (this) {
            PressureUnit.HectoPascal -> Res.string.unit_pressure_hecto_pascal
            PressureUnit.InchMercury -> Res.string.unit_pressure_inch_mercury
        }
        is TemperatureUnit -> when (this) {
            TemperatureUnit.Kelvin -> Res.string.unit_temperature_kelvin
            TemperatureUnit.Celsius -> Res.string.unit_temperature_celsius
            TemperatureUnit.Fahrenheit -> Res.string.unit_temperature_fahrenheit
        }
        is WindSpeedUnit -> when (this) {
            WindSpeedUnit.MeterPerSecond -> Res.string.unit_wind_ms
            WindSpeedUnit.KilometerPerHour -> Res.string.unit_wind_kph
            WindSpeedUnit.MilePerHour -> Res.string.unit_wind_mph
        }
    }

@Composable
public fun BaseUnit.rememberLabel(): String {
    val res = remember(this) { labelResource() }
    return res.get()
}

public fun BaseUnit.icon(): ImageVector =
    when (this) {
        is PrecipitationUnit -> AppIcons.Lucide.CloudRain
        is PressureUnit -> AppIcons.Lucide.Waves
        is TemperatureUnit -> AppIcons.Lucide.Thermometer
        is WindSpeedUnit -> AppIcons.Lucide.Wind
    }

@Composable
public fun BaseUnit.rememberIcon(): ImageVector = remember(this) { icon() }

@Composable
public fun BaseUnit.colors(): BrutalColors =
    when (this) {
        is PrecipitationUnit -> AppTheme.colors.brutal.blue
        is PressureUnit -> AppTheme.colors.brutal.pink
        is TemperatureUnit -> AppTheme.colors.brutal.red
        is WindSpeedUnit -> AppTheme.colors.brutal.green
    }
