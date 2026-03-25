package now.shouldigooutside.forecast.ui.forecast.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_details_cloud_cover
import now.shouldigooutside.core.resources.forecast_details_humidity
import now.shouldigooutside.core.resources.forecast_details_precipitation
import now.shouldigooutside.core.resources.forecast_details_pressure
import now.shouldigooutside.core.resources.forecast_details_uv
import now.shouldigooutside.core.resources.forecast_details_uv_index
import now.shouldigooutside.core.resources.forecast_details_visibility
import now.shouldigooutside.core.resources.forecast_details_wind
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.CloudRain
import now.shouldigooutside.core.ui.icons.lucide.Droplet
import now.shouldigooutside.core.ui.icons.lucide.Sun
import now.shouldigooutside.core.ui.icons.lucide.Waves
import now.shouldigooutside.core.ui.icons.lucide.Wind
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.units.rememberUnit
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.ForecastPreviewData.ForecastBlockPreviewParameterProvider
import now.shouldigooutside.forecast.ui.components.PreferenceResultCard
import kotlin.math.roundToInt

@Composable
internal fun WeatherDetailsGrid(
    block: ForecastBlock,
    units: Units,
    modifier: Modifier = Modifier,
) {
    val windUnit = units.windSpeed.rememberUnit()
    val pressureUnit = units.pressure.rememberUnit()
    val precipUnit = units.precipitation.rememberUnit()
    val visibilityText = formatVisibility(block.visibility, units.pressure)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.standard),
            maxItemsInEachRow = 2,
            modifier = Modifier.fillMaxWidth(),
        ) {
            PreferenceResultCard(
                title = Res.string.forecast_details_wind.get(),
                text = formatValueWithUnit(block.wind.speed.roundToInt(), windUnit),
                colors = AppTheme.colors.brutal.pink,
                value = { block.wind.formatGust(windUnit) },
                icon = AppIcons.Lucide.Wind,
                height = null,
                modifier = Modifier.weight(1f),
            )

            val precipType = block.precipitation.defaultType()
            PreferenceResultCard(
                title = Res.string.forecast_details_precipitation.get(),
                text = formatValueWithUnit(block.precipitation.amount, precipUnit),
                colors = AppTheme.colors.brutal.blue,
                value = { block.precipitation.formatChance(precipType) },
                icon = AppIcons.Lucide.CloudRain,
                height = null,
                modifier = Modifier.weight(1f),
            )

            PreferenceResultCard(
                title = Res.string.forecast_details_uv_index.get(),
                text = Res.string.forecast_details_uv.get(block.uvIndex),
                colors = AppTheme.colors.brutal.yellow,
                value = { uvIndexLabel(block.uvIndex) },
                icon = AppIcons.Lucide.Sun,
                height = null,
                modifier = Modifier.weight(1f),
            )

            PreferenceResultCard(
                title = Res.string.forecast_details_humidity.get(),
                text = block.humidity.roundToInt().formatPercent(),
                colors = AppTheme.colors.brutal.blue,
                value = { "" },
                icon = AppIcons.Lucide.Droplet,
                height = null,
                modifier = Modifier.weight(1f),
            )

            PreferenceResultCard(
                title = Res.string.forecast_details_pressure.get(),
                text = formatValueWithUnit(block.pressure.roundToInt(), pressureUnit),
                colors = AppTheme.colors.brutal.green,
                value = { "" },
                icon = AppIcons.Lucide.Waves,
                height = null,
                modifier = Modifier.weight(1f),
            )

            PreferenceResultCard(
                title = Res.string.forecast_details_cloud_cover.get(),
                text = block.cloudCoverPercent.formatPercent(),
                colors = AppTheme.colors.brutal.blue,
                value = { "" },
                icon = AppIcons.Lucide.CloudRain,
                height = null,
                modifier = Modifier.weight(1f),
            )

            PreferenceResultCard(
                title = Res.string.forecast_details_visibility.get(),
                text = visibilityText,
                colors = AppTheme.colors.brutal.purple,
                value = { "" },
                icon = AppIcons.Lucide.Sun,
                height = null,
                modifier = Modifier.weight(1f),
            )

            // Odd number of tiles
            Spacer(Modifier.weight(1f))
        }
    }
}

@Preview(name = "Light")
@Composable
private fun WeatherDetailsGridPreview(
    @PreviewParameter(ForecastBlockPreviewParameterProvider::class) block: ForecastBlock,
) {
    AppPreview {
        WeatherDetailsGrid(
            block = block,
            units = Units.Metric,
            modifier = Modifier.padding(16.dp),
        )
    }
}
