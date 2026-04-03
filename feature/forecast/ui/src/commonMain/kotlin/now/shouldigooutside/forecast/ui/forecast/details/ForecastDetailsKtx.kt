package now.shouldigooutside.forecast.ui.forecast.details

import androidx.compose.runtime.Composable
import now.shouldigooutside.core.model.forecast.Precipitation
import now.shouldigooutside.core.model.forecast.Temperature
import now.shouldigooutside.core.model.forecast.Wind
import now.shouldigooutside.core.model.units.PressureUnit
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_details_feels_like
import now.shouldigooutside.core.resources.forecast_details_high
import now.shouldigooutside.core.resources.forecast_details_low
import now.shouldigooutside.core.resources.forecast_details_percent
import now.shouldigooutside.core.resources.forecast_details_precipitation_chance
import now.shouldigooutside.core.resources.forecast_details_precipitation_type_default
import now.shouldigooutside.core.resources.forecast_details_temperature_value
import now.shouldigooutside.core.resources.forecast_details_uv_extreme
import now.shouldigooutside.core.resources.forecast_details_uv_high
import now.shouldigooutside.core.resources.forecast_details_uv_low
import now.shouldigooutside.core.resources.forecast_details_uv_moderate
import now.shouldigooutside.core.resources.forecast_details_uv_very_high
import now.shouldigooutside.core.resources.forecast_details_value_with_unit
import now.shouldigooutside.core.resources.forecast_details_visibility_km
import now.shouldigooutside.core.resources.forecast_details_visibility_miles
import now.shouldigooutside.core.resources.forecast_details_wind_direction_e
import now.shouldigooutside.core.resources.forecast_details_wind_direction_n
import now.shouldigooutside.core.resources.forecast_details_wind_direction_ne
import now.shouldigooutside.core.resources.forecast_details_wind_direction_nw
import now.shouldigooutside.core.resources.forecast_details_wind_direction_s
import now.shouldigooutside.core.resources.forecast_details_wind_direction_se
import now.shouldigooutside.core.resources.forecast_details_wind_direction_sw
import now.shouldigooutside.core.resources.forecast_details_wind_direction_w
import now.shouldigooutside.core.resources.forecast_details_wind_gust
import now.shouldigooutside.core.ui.ktx.get
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
internal fun Wind.formatGust(unit: String): String =
    Res.string.forecast_details_wind_gust.get(gust.roundToInt(), unit)

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
