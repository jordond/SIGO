package app.sigot.forecast.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.units.Units
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_details_cloud_cover
import app.sigot.core.resources.forecast_details_humidity
import app.sigot.core.resources.forecast_details_precipitation
import app.sigot.core.resources.forecast_details_pressure
import app.sigot.core.resources.forecast_details_uv_index
import app.sigot.core.resources.forecast_details_visibility
import app.sigot.core.resources.forecast_details_wind
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.BrutalColors
import app.sigot.core.ui.brutal
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.CloudRain
import app.sigot.core.ui.icons.lucide.Droplet
import app.sigot.core.ui.icons.lucide.Sun
import app.sigot.core.ui.icons.lucide.Waves
import app.sigot.core.ui.icons.lucide.Wind
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.mappers.units.rememberUnit
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.ForecastPreviewData.ForecastBlockPreviewParameterProvider
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

    val windDirection = degreesToCardinal(block.wind.directionDegree)
    val visibilityText = formatVisibility(block.visibility, units.pressure)

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherDetailTile(
                title = Res.string.forecast_details_humidity.get(),
                value = block.humidity.roundToInt().formatPercent(),
                icon = AppIcons.Lucide.Droplet,
                colors = AppTheme.colors.brutal.blue,
                modifier = Modifier.weight(1f),
            )

            WeatherDetailTile(
                title = Res.string.forecast_details_wind.get(),
                value = formatValueWithUnit(block.wind.speed.roundToInt(), windUnit),
                subtitle = block.wind.formatGust(windUnit, windDirection),
                icon = AppIcons.Lucide.Wind,
                colors = AppTheme.colors.brutal.pink,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherDetailTile(
                title = Res.string.forecast_details_uv_index.get(),
                value = "${block.uvIndex}",
                subtitle = uvIndexLabel(block.uvIndex),
                icon = AppIcons.Lucide.Sun,
                colors = AppTheme.colors.brutal.yellow,
                modifier = Modifier.weight(1f),
            )
            WeatherDetailTile(
                title = Res.string.forecast_details_pressure.get(),
                value = formatValueWithUnit(block.pressure.roundToInt(), pressureUnit),
                icon = AppIcons.Lucide.Waves,
                colors = AppTheme.colors.brutal.green,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            WeatherDetailTile(
                title = Res.string.forecast_details_visibility.get(),
                value = visibilityText,
                icon = AppIcons.Lucide.Sun,
                colors = AppTheme.colors.brutal.purple,
                modifier = Modifier.weight(1f),
            )
            WeatherDetailTile(
                title = Res.string.forecast_details_cloud_cover.get(),
                value = block.cloudCoverPercent.formatPercent(),
                icon = AppIcons.Lucide.CloudRain,
                colors = AppTheme.colors.brutal.blue,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            val precipType = block.precipitation.defaultType()
            WeatherDetailTile(
                title = Res.string.forecast_details_precipitation.get(),
                value = formatValueWithUnit(block.precipitation.amount, precipUnit),
                subtitle = block.precipitation.formatChance(precipType),
                icon = AppIcons.Lucide.CloudRain,
                colors = AppTheme.colors.brutal.blue,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
internal fun WeatherDetailTile(
    title: String,
    value: String,
    icon: ImageVector,
    colors: BrutalColors,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(colors.bright)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Icon(
                    icon = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = AppTheme.typography.h4,
                    maxLines = 1,
                )
            }

            HorizontalDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
            ) {
                Text(
                    text = value,
                    style = AppTheme.typography.h2,
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = AppTheme.typography.body1.copy(fontSize = 12.sp),
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
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

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun WeatherDetailTilePreview() {
    AppPreview {
        WeatherDetailTile(
            title = "Humidity",
            value = "65%",
            icon = AppIcons.Lucide.Droplet,
            colors = AppTheme.colors.brutal.blue,
            modifier = Modifier.padding(16.dp).width(160.dp),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun WeatherDetailTileWithSubtitlePreview() {
    AppPreview {
        WeatherDetailTile(
            title = "Wind",
            value = "15 km/h",
            subtitle = "Gust 25 km/h NE",
            icon = AppIcons.Lucide.Wind,
            colors = AppTheme.colors.brutal.pink,
            modifier = Modifier.padding(16.dp).width(160.dp),
        )
    }
}
