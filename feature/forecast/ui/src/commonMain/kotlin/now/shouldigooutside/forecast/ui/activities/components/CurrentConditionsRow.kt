package now.shouldigooutside.forecast.ui.activities.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.percent
import now.shouldigooutside.core.resources.unit_precipitation_rain
import now.shouldigooutside.core.resources.unit_precipitation_snow
import now.shouldigooutside.core.resources.unit_temperature_short
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalAppExperience
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.mappers.units.rememberTitle
import now.shouldigooutside.core.ui.mappers.units.rememberUnit
import now.shouldigooutside.forecast.ui.components.AirQualityResultCard
import now.shouldigooutside.forecast.ui.components.PreferenceResultCard
import kotlin.math.roundToInt

@Composable
internal fun CurrentConditionsRow(
    block: ForecastBlock,
    units: Units,
    modifier: Modifier = Modifier,
) {
    val includeAqi = LocalAppExperience.current.includeAirQuality
    val spacing = AppTheme.spacing.small

    if (includeAqi) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth(),
            ) {
                TemperatureCard(block, units, Modifier.weight(1f))
                WindCard(block, units, Modifier.weight(1f))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing),
                modifier = Modifier.fillMaxWidth(),
            ) {
                PrecipitationCard(block, units, Modifier.weight(1f))
                AirQualityResultCard(
                    airQuality = block.airQuality,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing),
            modifier = modifier.fillMaxWidth(),
        ) {
            TemperatureCard(block, units, Modifier.weight(1f))
            WindCard(block, units, Modifier.weight(1f))
            PrecipitationCard(block, units, Modifier.weight(1f))
        }
    }
}

@Composable
private fun TemperatureCard(
    block: ForecastBlock,
    units: Units,
    modifier: Modifier = Modifier,
) {
    PreferenceResultCard(
        title = Res.string.unit_temperature_short.get(),
        text = "${block.temperature.value.roundToInt()}${units.temperature.rememberUnit()}",
        colors = units.temperature.colors(),
        value = { "" },
        modifier = modifier,
    )
}

@Composable
private fun WindCard(
    block: ForecastBlock,
    units: Units,
    modifier: Modifier = Modifier,
) {
    PreferenceResultCard(
        title = units.windSpeed.rememberTitle(),
        text = "${block.wind.speed.roundToInt()} ${units.windSpeed.rememberUnit()}",
        colors = units.windSpeed.colors(),
        value = { "" },
        modifier = modifier,
    )
}

@Composable
private fun PrecipitationCard(
    block: ForecastBlock,
    units: Units,
    modifier: Modifier = Modifier,
) {
    val title = remember(block.precipitation) {
        if (block.precipitation.isRain) {
            Res.string.unit_precipitation_rain
        } else {
            Res.string.unit_precipitation_snow
        }
    }.get()

    PreferenceResultCard(
        title = title,
        text = Res.string.percent.get(block.precipitation.probability),
        colors = units.precipitation.colors(),
        value = { "" },
        modifier = modifier,
    )
}
