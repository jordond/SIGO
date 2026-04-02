package now.shouldigooutside.forecast.ui.activities.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.ui.AppExperience
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.activities_current_conditions
import now.shouldigooutside.core.resources.percent
import now.shouldigooutside.core.resources.unit_air_quality_short
import now.shouldigooutside.core.resources.unit_precipitation_rain
import now.shouldigooutside.core.resources.unit_precipitation_snow
import now.shouldigooutside.core.resources.unit_temperature_short
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.AqiLevels
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.LocalAppExperience
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Surface
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.VerticalDivider
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.CloudRain
import now.shouldigooutside.core.ui.icons.lucide.Snowflake
import now.shouldigooutside.core.ui.icons.lucide.Thermometer
import now.shouldigooutside.core.ui.icons.lucide.Waves
import now.shouldigooutside.core.ui.icons.lucide.Wind
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.mappers.units.rememberTitle
import now.shouldigooutside.core.ui.mappers.units.rememberUnit
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import kotlin.math.roundToInt

@Composable
internal fun CurrentConditionsCard(
    block: ForecastBlock,
    units: Units,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val includeAqi = LocalAppExperience.current.includeAirQuality

    ElevatedCard(modifier = modifier, onClick = onClick) {
        Column {
            Text(
                text = Res.string.activities_current_conditions.get(),
                style = AppTheme.typography.h4,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.surface)
                    .padding(vertical = AppTheme.spacing.small, horizontal = AppTheme.spacing.standard),
            )

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
            ) {
                ConditionItem(
                    icon = AppIcons.Lucide.Thermometer,
                    value = "${block.temperature.value.roundToInt()}${units.temperature.rememberUnit()}",
                    label = Res.string.unit_temperature_short.get(),
                    colors = units.temperature.colors(),
                    modifier = Modifier.weight(1f),
                )

                VerticalDivider()

                ConditionItem(
                    icon = AppIcons.Lucide.Wind,
                    value = "${block.wind.speed.roundToInt()} ${units.windSpeed.rememberUnit()}",
                    label = units.windSpeed.rememberTitle(),
                    colors = units.windSpeed.colors(),
                    modifier = Modifier.weight(1f),
                )

                VerticalDivider()

                val isRain = remember(block.precipitation) { block.precipitation.isRain }
                ConditionItem(
                    icon = if (isRain) AppIcons.Lucide.CloudRain else AppIcons.Lucide.Snowflake,
                    value = Res.string.percent.get(block.precipitation.probability),
                    label = if (isRain) {
                        Res.string.unit_precipitation_rain.get()
                    } else {
                        Res.string.unit_precipitation_snow.get()
                    },
                    colors = units.precipitation.colors(),
                    modifier = Modifier.weight(1f),
                )

                if (includeAqi) {
                    VerticalDivider()

                    val aqiLevel = AqiLevels.forValue(block.airQuality)
                    ConditionItem(
                        icon = AppIcons.Lucide.Waves,
                        value = aqiLevel.title.get(),
                        label = Res.string.unit_air_quality_short.get(),
                        colors = aqiLevel.colors,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ConditionItem(
    icon: ImageVector,
    value: String,
    label: String,
    colors: BrutalColors,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = colors.container,
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(
                vertical = AppTheme.spacing.small,
                horizontal = AppTheme.spacing.mini,
            ),
        ) {
            Icon(
                icon = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = value,
                style = AppTheme.typography.h3,
                autoSize = AppTheme.typography.h3.autoSize(),
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
            Text(
                text = label,
                style = AppTheme.typography.label3,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun CurrentConditionsCardPreview() {
    val forecast = PreviewData.Forecast.createForecast()
    AppPreview {
        Column {
            CurrentConditionsCard(
                block = forecast.current,
                units = Units.Metric,
                onClick = {},
                modifier = Modifier.padding(16.dp),
            )

            CompositionLocalProvider(
                LocalAppExperience provides AppExperience.default.copy(includeAirQuality = false),
            ) {
                CurrentConditionsCard(
                    block = forecast.current,
                    units = Units.Metric,
                    onClick = {},
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}
