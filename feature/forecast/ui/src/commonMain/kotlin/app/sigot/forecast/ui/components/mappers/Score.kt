package app.sigot.forecast.ui.components.mappers

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import app.sigot.core.model.ForecastPeriodData
import app.sigot.core.model.score.ReasonValue
import app.sigot.core.model.score.Reasons
import app.sigot.core.model.score.ScoreResult
import app.sigot.core.resources.Res
import app.sigot.core.resources.score_maybe
import app.sigot.core.resources.score_no
import app.sigot.core.resources.score_precipitation_rain_inside
import app.sigot.core.resources.score_precipitation_rain_near
import app.sigot.core.resources.score_precipitation_rain_outside
import app.sigot.core.resources.score_precipitation_snow_inside
import app.sigot.core.resources.score_precipitation_snow_near
import app.sigot.core.resources.score_precipitation_snow_outside
import app.sigot.core.resources.score_precipitation_status_inside
import app.sigot.core.resources.score_precipitation_status_near
import app.sigot.core.resources.score_precipitation_status_outside
import app.sigot.core.resources.score_temperature_inside
import app.sigot.core.resources.score_temperature_near
import app.sigot.core.resources.score_temperature_outside_high
import app.sigot.core.resources.score_temperature_outside_low
import app.sigot.core.resources.score_temperature_status_inside
import app.sigot.core.resources.score_temperature_status_near
import app.sigot.core.resources.score_temperature_status_outside_high
import app.sigot.core.resources.score_temperature_status_outside_low
import app.sigot.core.resources.score_wind_inside
import app.sigot.core.resources.score_wind_near
import app.sigot.core.resources.score_wind_outside
import app.sigot.core.resources.score_wind_status_inside
import app.sigot.core.resources.score_wind_status_near
import app.sigot.core.resources.score_wind_status_outside
import app.sigot.core.resources.score_yes
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.BrutalColors
import app.sigot.core.ui.brutal
import app.sigot.core.ui.ktx.get

@Composable
internal fun ForecastPeriodData.brutalColor(): BrutalColors =
    when (score.result) {
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
            ReasonValue.Inside -> Res.string.score_temperature_status_inside
            ReasonValue.Near -> Res.string.score_temperature_status_near
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
            ReasonValue.Inside -> Res.string.score_temperature_inside
            ReasonValue.Near -> Res.string.score_temperature_near
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
