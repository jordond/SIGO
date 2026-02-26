package app.sigot.forecast.ui.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.model.ForecastPeriodData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.forecast.SevereWeatherRisk
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.score.ReasonValue
import app.sigot.core.model.units.Units
import app.sigot.core.resources.Res
import app.sigot.core.resources.percent
import app.sigot.core.resources.score_severe_weather_near
import app.sigot.core.resources.score_severe_weather_outside
import app.sigot.core.resources.unit_precipitation_rain
import app.sigot.core.resources.unit_precipitation_snow
import app.sigot.core.resources.unit_temperature_short
import app.sigot.core.resources.updated_at
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.Card
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.OctagonAlert
import app.sigot.core.ui.icons.lucide.TriangleAlert
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.ktx.rememberTimeAgo
import app.sigot.core.ui.mappers.units.colors
import app.sigot.core.ui.mappers.units.rememberTitle
import app.sigot.core.ui.mappers.units.rememberUnit
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.PreviewData
import app.sigot.forecast.ui.components.PreferenceResultCard
import app.sigot.forecast.ui.components.mappers.colors
import app.sigot.forecast.ui.components.mappers.precipitationStatus
import app.sigot.forecast.ui.components.mappers.rememberScoreText
import app.sigot.forecast.ui.components.mappers.temperatureStatus
import app.sigot.forecast.ui.components.mappers.windStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@Composable
internal fun ForecastScoreContent(
    updatedAt: Instant,
    preferences: Preferences,
    periodData: ForecastPeriodData,
    modifier: Modifier = Modifier,
    now: Instant = Clock.System.now(),
) {
    val elevation = CardDefaults.cardElevation()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
        modifier = modifier
            .padding(horizontal = elevation.default)
            .padding(bottom = elevation.default),
    ) {
        val (containerColor, contentColor) = periodData.colors()
        ElevatedCard(
            elevation = elevation,
            colors = CardDefaults.elevatedCardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
            modifier = Modifier
                .weight(2f, fill = false)
                .heightIn(min = 100.dp, max = 300.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxSize(),
            ) {
                val text = periodData.rememberScoreText()
                Text(
                    text = text,
                    maxLines = 1,
                    autoSize = TextAutoSize.StepBased(maxFontSize = 200.sp),
                    style = AppTheme.typography.h2.copy(letterSpacing = (-20).sp),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
        ) {
            val text = remember(periodData.score.reasons.severeWeather) {
                when (periodData.score.reasons.severeWeather) {
                    ReasonValue.Inside -> null
                    ReasonValue.Near -> Res.string.score_severe_weather_near
                    ReasonValue.Outside -> Res.string.score_severe_weather_outside
                }
            }
            AnimatedVisibility(
                visible = text != null,
                modifier = Modifier.padding(top = AppTheme.spacing.small),
            ) {
                val colors = when (periodData.score.reasons.severeWeather) {
                    ReasonValue.Inside -> CardDefaults.cardColors()
                    ReasonValue.Near -> CardDefaults.primaryColors
                    ReasonValue.Outside -> CardDefaults.errorColors
                }

                Card(
                    colors = colors,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .padding(
                                vertical = AppTheme.spacing.small,
                                horizontal = AppTheme.spacing.standard,
                            ),
                    ) {
                        val icon = remember(periodData.score.reasons.severeWeather) {
                            if (periodData.score.reasons.severeWeather == ReasonValue.Outside) {
                                AppIcons.Lucide.OctagonAlert
                            } else {
                                AppIcons.Lucide.TriangleAlert
                            }
                        }
                        Icon(icon)
                        Text(text = text?.get() ?: "")
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.standard))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                PreferenceResultCard(
                    title = Res.string.unit_temperature_short.get(),
                    text = periodData.score.reasons.temperatureStatus(
                        value = periodData.forecast.temperature.value,
                        max = preferences.maxTemperature.toDouble(),
                    ),
                    colors = preferences.units.temperature.colors(),
                    value = {
                        val unit = preferences.units.temperature.rememberUnit()
                        "${periodData.forecast.temperature.value.roundToInt()}$unit"
                    },
                    modifier = Modifier.weight(1f),
                )

                PreferenceResultCard(
                    title = preferences.units.windSpeed.rememberTitle(),
                    text = periodData.score.reasons.windStatus(),
                    colors = preferences.units.windSpeed.colors(),
                    value = {
                        val unit = preferences.units.windSpeed.rememberUnit()
                        "${periodData.forecast.wind.speed.roundToInt()} $unit"
                    },
                    modifier = Modifier.weight(1f),
                )

                val precipitationTitle = remember(periodData.forecast.precipitation) {
                    if (periodData.forecast.precipitation.isRain) {
                        Res.string.unit_precipitation_rain
                    } else {
                        Res.string.unit_precipitation_snow
                    }
                }.get()

                PreferenceResultCard(
                    title = precipitationTitle,
                    text = periodData.score.reasons.precipitationStatus(),
                    colors = preferences.units.precipitation.colors(),
                    value = {
                        Res.string.percent.get(periodData.forecast.precipitation.probability)
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.small))

            UpdatedAtText(instant = updatedAt)
        }
    }
}

@Composable
internal fun UpdatedAtText(
    instant: Instant,
    modifier: Modifier = Modifier,
    delay: Duration = 30.seconds,
    nowProvider: () -> Instant = { Clock.System.now() },
) {
    var now by remember(nowProvider) { mutableStateOf(nowProvider()) }
    LaunchedEffect(delay) {
        while (isActive && delay > Duration.ZERO) {
            delay(delay)
            now = nowProvider()
        }
    }

    val updatedText = Res.string.updated_at.get(instant.rememberTimeAgo(now = now))
    Text(
        text = updatedText,
        style = AppTheme.typography.label3.copy(
            fontStyle = FontStyle.Italic,
        ),
        modifier = modifier,
    )
}

@Preview
@Composable
private fun SevereWeatherPreview() {
    val forecast = PreviewData.Forecast.createForecastFrom(
        PreviewData.Forecast.severeWeather(
            level = SevereWeatherRisk.Low,
        ),
    )
    val data = PreviewData.Forecast.forecastData(forecast)
    AppPreview {
        Box(
            modifier = Modifier
                .padding(horizontal = AppTheme.spacing.standard, vertical = AppTheme.spacing.standard)
                .height(700.dp),
        ) {
            ForecastScoreContent(
                updatedAt = Clock.System.now().minus(1.minutes),
                preferences = Preferences.default.copy(units = Units.Metric),
                periodData = data.forPeriod(ForecastPeriod.Today)!!,
            )
        }
    }
}

@Preview
@Composable
private fun ForecastScoreContentPreview() {
    val data = PreviewData.Forecast.forecastData(PreviewData.Forecast.createWindyForecast())
    AppPreview {
        Box(
            modifier = Modifier
                .padding(horizontal = AppTheme.spacing.standard, vertical = AppTheme.spacing.standard)
                .height(700.dp),
        ) {
            ForecastScoreContent(
                updatedAt = Clock.System.now().minus(1.minutes),
                preferences = Preferences.default.copy(units = Units.Metric),
                periodData = data.forPeriod(ForecastPeriod.Today)!!,
            )
        }
    }
}
