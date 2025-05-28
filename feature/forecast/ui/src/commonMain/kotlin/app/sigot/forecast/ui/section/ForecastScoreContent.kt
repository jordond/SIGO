package app.sigot.forecast.ui.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.model.ForecastPeriodData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.preferences.Preferences
import app.sigot.core.model.units.Units
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_view_details
import app.sigot.core.resources.unit_precipitation_rain
import app.sigot.core.resources.unit_precipitation_snow
import app.sigot.core.resources.unit_temperature_short
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.mappers.units.colors
import app.sigot.core.ui.mappers.units.rememberTitle
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.PreviewData
import app.sigot.forecast.ui.components.PreferenceResultCard
import app.sigot.forecast.ui.components.mappers.colors
import app.sigot.forecast.ui.components.mappers.precipitationStatus
import app.sigot.forecast.ui.components.mappers.rememberScoreText
import app.sigot.forecast.ui.components.mappers.temperatureStatus
import app.sigot.forecast.ui.components.mappers.windStatus
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun ForecastScoreContent(
    preferences: Preferences,
    periodData: ForecastPeriodData,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
    ) {
        val (containerColor, contentColor) = periodData.colors()
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
            modifier = Modifier
                .weight(2f)
                .heightIn(min = 200.dp, max = 400.dp),
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
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            modifier = Modifier
                .weight(1f)
                .padding(top = AppTheme.spacing.small),
        ) {
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
                    modifier = Modifier.weight(1f),
                )

                PreferenceResultCard(
                    title = preferences.units.windSpeed.rememberTitle(),
                    text = periodData.score.reasons.windStatus(),
                    colors = preferences.units.windSpeed.colors(),
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
                    modifier = Modifier.weight(1f),
                )
            }

            Button(
                variant = ButtonVariant.PrimaryElevated,
                text = Res.string.forecast_view_details.get(),
                textStyle = AppTheme.typography.h2,
                onClick = onViewDetails,
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
                preferences = Preferences.default.copy(units = Units.Metric),
                periodData = data.forPeriod(ForecastPeriod.Today)!!,
                onViewDetails = {},
            )
        }
    }
}
