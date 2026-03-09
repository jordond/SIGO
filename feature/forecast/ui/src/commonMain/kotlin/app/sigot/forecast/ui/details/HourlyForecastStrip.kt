package app.sigot.forecast.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.model.forecast.ForecastBlock
import app.sigot.core.model.units.Units
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.BrutalColors
import app.sigot.core.ui.brutal
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Droplet
import app.sigot.core.ui.ktx.text
import app.sigot.core.ui.mappers.units.rememberUnit
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.ForecastPreviewData
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

@Composable
internal fun HourlyForecastStrip(
    hours: PersistentList<ForecastBlock>,
    selected: ForecastBlock,
    units: Units,
    onHourSelected: (ForecastBlock) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tempUnit = units.temperature.rememberUnit()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(hours, key = { it.instant.toString() }) { hour ->
            val isSelected = selected == hour
            val colors = if (isSelected) {
                AppTheme.colors.brutal.yellow
            } else {
                AppTheme.colors.brutal.blue
            }

            HourCard(
                block = hour,
                tempUnit = tempUnit,
                colors = colors,
                isSelected = isSelected,
                onClick = { onHourSelected(hour) },
            )
        }
    }
}

@Composable
internal fun HourCard(
    block: ForecastBlock,
    tempUnit: String,
    colors: BrutalColors,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val localTime = remember(block.instant) {
        LocalTime(hour = block.instant.toLocalDateTime(TimeZone.currentSystemDefault()).hour, minute = 0)
    }
    val hour = localTime.text()

    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) colors.bright else AppTheme.colors.surface,
        ),
        modifier = modifier.width(80.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
        ) {
            Text(
                text = hour,
                style = AppTheme.typography.h4,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = block.temperature.formatValue(tempUnit),
                style = AppTheme.typography.h3,
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (block.precipitation.probability > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        icon = AppIcons.Lucide.Droplet,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                    )
                    Text(
                        text = block.precipitation.probability.formatPercent(),
                        style = AppTheme.typography.body1.copy(fontSize = 11.sp),
                    )
                }
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun HourlyForecastStripPreview() {
    val now = kotlin.time.Clock.System
        .now()
    AppPreview {
        HourlyForecastStrip(
            hours = persistentListOf(
                ForecastPreviewData.sunny(now.plus(1.hours)),
                ForecastPreviewData.rainy(now.plus(2.hours)),
                ForecastPreviewData.sunny(now.plus(3.hours)),
                ForecastPreviewData.windy(now.plus(4.hours)),
                ForecastPreviewData.hot(now.plus(5.hours)),
            ),
            selected = ForecastPreviewData.sunny(now.plus(1.hours)),
            units = Units.Metric,
            onHourSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun HourCardSelectedPreview() {
    AppPreview {
        HourCard(
            block = ForecastPreviewData.rainy(),
            tempUnit = "°C",
            colors = AppTheme.colors.brutal.yellow,
            isSelected = true,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun HourCardUnselectedPreview() {
    AppPreview {
        HourCard(
            block = ForecastPreviewData.sunny(),
            tempUnit = "°C",
            colors = AppTheme.colors.brutal.blue,
            isSelected = false,
            onClick = {},
        )
    }
}
