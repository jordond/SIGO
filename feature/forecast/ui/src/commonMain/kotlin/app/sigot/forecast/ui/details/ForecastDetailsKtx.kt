package app.sigot.forecast.ui.details

import androidx.compose.runtime.Composable
import app.sigot.core.model.forecast.Precipitation
import app.sigot.core.model.forecast.Temperature
import app.sigot.core.model.forecast.Wind
import app.sigot.core.model.units.PressureUnit
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_details_feels_like
import app.sigot.core.resources.forecast_details_high
import app.sigot.core.resources.forecast_details_low
import app.sigot.core.resources.forecast_details_percent
import app.sigot.core.resources.forecast_details_precipitation_chance
import app.sigot.core.resources.forecast_details_precipitation_type_default
import app.sigot.core.resources.forecast_details_temperature_value
import app.sigot.core.resources.forecast_details_uv_extreme
import app.sigot.core.resources.forecast_details_uv_high
import app.sigot.core.resources.forecast_details_uv_low
import app.sigot.core.resources.forecast_details_uv_moderate
import app.sigot.core.resources.forecast_details_uv_very_high
import app.sigot.core.resources.forecast_details_value_with_unit
import app.sigot.core.resources.forecast_details_visibility_km
import app.sigot.core.resources.forecast_details_visibility_miles
import app.sigot.core.resources.forecast_details_wind_direction_e
import app.sigot.core.resources.forecast_details_wind_direction_n
import app.sigot.core.resources.forecast_details_wind_direction_ne
import app.sigot.core.resources.forecast_details_wind_direction_nw
import app.sigot.core.resources.forecast_details_wind_direction_s
import app.sigot.core.resources.forecast_details_wind_direction_se
import app.sigot.core.resources.forecast_details_wind_direction_sw
import app.sigot.core.resources.forecast_details_wind_direction_w
import app.sigot.core.resources.forecast_details_wind_gust
import app.sigot.core.ui.ktx.get
import kotlin.math.roundToInt

@Composable
internal fun Temperature.formatValue(unit: String): String =
    Res.string.forecast_details_temperature_value.get(value.roundToInt(), unit)

@Composable
internal fun Temperature.formatFeelsLike(unit: String): String =
    Res.string.forecast_details_feels_like.get(feelsLike.roundToInt(), unit)

@Composable
internal fun Temperature.formatHigh(unit: String): String =
    Res.string.forecast_details_high.get(max.roundToInt(), unit)

@Composable
internal fun Temperature.formatLow(unit: String): String =
    Res.string.forecast_details_low.get(min.roundToInt(), unit)

@Composable
internal fun Int.formatPercent(): String = Res.string.forecast_details_percent.get(this)

@Composable
internal fun formatValueWithUnit(
    value: Number,
    unit: String,
): String = Res.string.forecast_details_value_with_unit.get(value.toString(), unit)

@Composable
internal fun Wind.formatGust(
    unit: String,
    direction: String,
): String = Res.string.forecast_details_wind_gust.get(gust.roundToInt(), unit, direction)

@Composable
internal fun Precipitation.formatChance(type: String): String =
    Res.string.forecast_details_precipitation_chance.get(probability, type)

@Composable
internal fun Precipitation.defaultType(): String {
    val default = Res.string.forecast_details_precipitation_type_default.get()
    return types.firstOrNull()?.name ?: default
}

@Composable
internal fun formatVisibility(
    visibility: Double,
    pressureUnit: PressureUnit,
): String =
    if (pressureUnit == PressureUnit.InchMercury) {
        Res.string.forecast_details_visibility_miles.get((visibility * 0.621371).roundToInt())
    } else {
        Res.string.forecast_details_visibility_km.get(visibility.roundToInt())
    }

@Composable
internal fun degreesToCardinal(degrees: Double): String {
    val normalized = ((degrees % 360) + 360) % 360
    return when {
        normalized !in 22.5..<337.5 -> Res.string.forecast_details_wind_direction_n.get()
        normalized < 67.5 -> Res.string.forecast_details_wind_direction_ne.get()
        normalized < 112.5 -> Res.string.forecast_details_wind_direction_e.get()
        normalized < 157.5 -> Res.string.forecast_details_wind_direction_se.get()
        normalized < 202.5 -> Res.string.forecast_details_wind_direction_s.get()
        normalized < 247.5 -> Res.string.forecast_details_wind_direction_sw.get()
        normalized < 292.5 -> Res.string.forecast_details_wind_direction_w.get()
        else -> Res.string.forecast_details_wind_direction_nw.get()
    }
}

@Composable
internal fun uvIndexLabel(uvIndex: Int): String =
    when {
        uvIndex <= 2 -> Res.string.forecast_details_uv_low.get()
        uvIndex <= 5 -> Res.string.forecast_details_uv_moderate.get()
        uvIndex <= 7 -> Res.string.forecast_details_uv_high.get()
        uvIndex <= 10 -> Res.string.forecast_details_uv_very_high.get()
        else -> Res.string.forecast_details_uv_extreme.get()
    }
