package now.shouldigooutside.forecast.ui.components.mappers

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import now.shouldigooutside.core.model.ForecastPeriodData
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.score_air_quality_inside
import now.shouldigooutside.core.resources.score_air_quality_near
import now.shouldigooutside.core.resources.score_air_quality_outside
import now.shouldigooutside.core.resources.score_air_quality_status_inside
import now.shouldigooutside.core.resources.score_air_quality_status_near
import now.shouldigooutside.core.resources.score_air_quality_status_outside
import now.shouldigooutside.core.resources.score_maybe
import now.shouldigooutside.core.resources.score_no
import now.shouldigooutside.core.resources.score_precipitation_rain_inside
import now.shouldigooutside.core.resources.score_precipitation_rain_near
import now.shouldigooutside.core.resources.score_precipitation_rain_outside
import now.shouldigooutside.core.resources.score_precipitation_snow_inside
import now.shouldigooutside.core.resources.score_precipitation_snow_near
import now.shouldigooutside.core.resources.score_precipitation_snow_outside
import now.shouldigooutside.core.resources.score_precipitation_status_inside
import now.shouldigooutside.core.resources.score_precipitation_status_near
import now.shouldigooutside.core.resources.score_precipitation_status_outside
import now.shouldigooutside.core.resources.score_temperature_inside
import now.shouldigooutside.core.resources.score_temperature_near
import now.shouldigooutside.core.resources.score_temperature_outside_high
import now.shouldigooutside.core.resources.score_temperature_outside_low
import now.shouldigooutside.core.resources.score_temperature_status_inside
import now.shouldigooutside.core.resources.score_temperature_status_near
import now.shouldigooutside.core.resources.score_temperature_status_outside_high
import now.shouldigooutside.core.resources.score_temperature_status_outside_low
import now.shouldigooutside.core.resources.score_wind_inside
import now.shouldigooutside.core.resources.score_wind_near
import now.shouldigooutside.core.resources.score_wind_outside
import now.shouldigooutside.core.resources.score_wind_status_inside
import now.shouldigooutside.core.resources.score_wind_status_near
import now.shouldigooutside.core.resources.score_wind_status_outside
import now.shouldigooutside.core.resources.score_yes
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.AqiLevels
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.ktx.get

@Composable
internal fun ForecastPeriodData.brutalColor(): BrutalColors = score.result.color()

@Composable
internal fun ScoreResult.color(): BrutalColors =
    when (this) {
        ScoreResult.Yes -> AppTheme.colors.brutal.green
        ScoreResult.Maybe -> AppTheme.colors.brutal.yellow
        ScoreResult.No -> AppTheme.colors.brutal.red
    }

@Composable
internal fun ForecastPeriodData.colors(): Pair<Color, Color> {
    val colors = brutalColor()
    val containerColor by animateColorAsState(colors.container)
    val contentColor = colors.containerContent

    return containerColor to contentColor
}

@Composable
internal fun ForecastPeriodData.rememberScoreText(): String = score.result.rememberText()

@Composable
internal fun ScoreResult.rememberText(): String {
    val res = remember(this) {
        when (this) {
            ScoreResult.Yes -> Res.string.score_yes
            ScoreResult.Maybe -> Res.string.score_maybe
            ScoreResult.No -> Res.string.score_no
        }
    }

    return res.get()
}

@Composable
internal fun Reasons.temperatureStatus(
    value: Double,
    max: Double,
): String =
    remember(temperature) {
        when (temperature) {
            ReasonValue.Inside -> {
                Res.string.score_temperature_status_inside
            }
            ReasonValue.Near -> {
                Res.string.score_temperature_status_near
            }
            ReasonValue.Outside -> {
                if (value >= max) {
                    Res.string.score_temperature_status_outside_high
                } else {
                    Res.string.score_temperature_status_outside_low
                }
            }
        }
    }.get()

@Composable
internal fun Reasons.temperatureText(
    value: Double,
    max: Double,
): String =
    remember(temperature) {
        when (temperature) {
            ReasonValue.Inside -> {
                Res.string.score_temperature_inside
            }
            ReasonValue.Near -> {
                Res.string.score_temperature_near
            }
            ReasonValue.Outside -> {
                if (value >= max) {
                    Res.string.score_temperature_outside_high
                } else {
                    Res.string.score_temperature_outside_low
                }
            }
        }
    }.get()

@Composable
internal fun Reasons.windStatus(): String =
    remember(wind) {
        when (wind) {
            ReasonValue.Inside -> Res.string.score_wind_status_inside
            ReasonValue.Near -> Res.string.score_wind_status_near
            ReasonValue.Outside -> Res.string.score_wind_status_outside
        }
    }.get()

@Composable
internal fun Reasons.windText(): String =
    remember(wind) {
        when (wind) {
            ReasonValue.Inside -> Res.string.score_wind_inside
            ReasonValue.Near -> Res.string.score_wind_near
            ReasonValue.Outside -> Res.string.score_wind_outside
        }
    }.get()

@Composable
internal fun Reasons.precipitationStatus(): String =
    remember(precipitation) {
        when (precipitation) {
            ReasonValue.Outside -> Res.string.score_precipitation_status_outside
            ReasonValue.Inside -> Res.string.score_precipitation_status_inside
            ReasonValue.Near -> Res.string.score_precipitation_status_near
        }
    }.get()

@Composable
internal fun Reasons.precipitationText(isRain: Boolean): String =
    remember(precipitation) {
        if (isRain) {
            when (precipitation) {
                ReasonValue.Outside -> Res.string.score_precipitation_rain_outside
                ReasonValue.Inside -> Res.string.score_precipitation_rain_inside
                ReasonValue.Near -> Res.string.score_precipitation_rain_near
            }
        } else {
            when (precipitation) {
                ReasonValue.Outside -> Res.string.score_precipitation_snow_outside
                ReasonValue.Inside -> Res.string.score_precipitation_snow_inside
                ReasonValue.Near -> Res.string.score_precipitation_snow_near
            }
        }
    }.get()

@Composable
internal fun Reasons.airQualityStatus(): String =
    remember(airQuality) {
        when (airQuality) {
            ReasonValue.Inside -> Res.string.score_air_quality_status_inside
            ReasonValue.Near -> Res.string.score_air_quality_status_near
            ReasonValue.Outside -> Res.string.score_air_quality_status_outside
        }
    }.get()

@Composable
internal fun Reasons.airQualityText(): String =
    remember(airQuality) {
        when (airQuality) {
            ReasonValue.Inside -> Res.string.score_air_quality_inside
            ReasonValue.Near -> Res.string.score_air_quality_near
            ReasonValue.Outside -> Res.string.score_air_quality_outside
        }
    }.get()

@Composable
internal fun aqiColors(aqi: AirQuality): BrutalColors = AqiLevels.forValue(aqi).colors
