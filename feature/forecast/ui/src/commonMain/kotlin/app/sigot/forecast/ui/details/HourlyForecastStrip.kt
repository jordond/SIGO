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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_period_now
import app.sigot.core.resources.forecast_period_tomorrow
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.brutal
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.autoSize
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Droplet
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.ktx.scrollToBottom
import app.sigot.core.ui.ktx.scrollToTop
import app.sigot.core.ui.mappers.units.rememberUnit
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.ForecastPreviewData
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Composable
internal fun HourlyForecastStrip(
    now: ForecastBlock,
    tomorrow: ForecastBlock?,
    hours: PersistentList<ForecastBlock>,
    selected: ForecastBlock?,
    units: Units,
    onSelected: (ForecastBlock?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tempUnit = units.temperature.rememberUnit()

    val initialFirst = remember {
        hours.indexOfFirst { it == (selected ?: now) }.takeIf { it != -1 } ?: 0
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialFirst)

    LaunchedEffect(selected) {
        when (selected) {
            null, now -> listState.scrollToTop()
            tomorrow -> listState.scrollToBottom()
            else -> {
                hours.indexOf(selected).takeIf { it != -1 }?.let { index ->
                    listState.animateScrollToItem(index)
                }
            }
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
        contentPadding = PaddingValues(horizontal = AppTheme.spacing.standard),
        state = listState,
        modifier = modifier.fillMaxWidth(),
    ) {
        item {
            HourCard(
                text = Res.string.forecast_period_now.get(),
                block = now,
                tempUnit = tempUnit,
                isSelected = now == selected || selected == null,
                onClick = { onSelected(null) },
            )
        }

        items(hours, key = { it.instant.toString() }) { hour ->
            val isSelected = selected == hour

            HourCard(
                block = hour,
                tempUnit = tempUnit,
                isSelected = isSelected,
                onClick = { onSelected(hour) },
            )
        }

        if (tomorrow != null) {
            item {
                HourCard(
                    text = Res.string.forecast_period_tomorrow.get(),
                    block = tomorrow,
                    tempUnit = tempUnit,
                    isSelected = tomorrow == selected,
                    onClick = { onSelected(tomorrow) },
                    modifier = modifier.animateItem(),
                )
            }
        }
    }
}

@Composable
internal fun HourCard(
    block: ForecastBlock,
    tempUnit: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String? = null,
) {
    val title = text ?: Res.string.forecast_period_now.get()

    val colors = if (isSelected) {
        AppTheme.colors.brutal.yellow
    } else {
        AppTheme.colors.brutal.blue
    }

    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) colors.bright else AppTheme.colors.surface,
        ),
        modifier = modifier.width(100.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
        ) {
            Text(
                text = title,
                style = AppTheme.typography.body1,
                maxLines = 1,
                autoSize = AppTheme.typography.body1.autoSize(),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = block.temperature.formatValue(tempUnit),
                style = AppTheme.typography.h3,
            )

            Spacer(modifier = Modifier.height(4.dp))

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

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun HourlyForecastStripPreview() {
    val now = kotlin.time.Clock.System
        .now()
    AppPreview {
        HourlyForecastStrip(
            now = ForecastPreviewData.sunny(now),
            hours = persistentListOf(
                ForecastPreviewData.sunny(now.plus(1.hours)),
                ForecastPreviewData.rainy(now.plus(2.hours)),
                ForecastPreviewData.sunny(now.plus(3.hours)),
                ForecastPreviewData.windy(now.plus(4.hours)),
                ForecastPreviewData.hot(now.plus(5.hours)),
            ),
            selected = ForecastPreviewData.sunny(now.plus(1.hours)),
            tomorrow = ForecastPreviewData.sunny(now.plus(1.days)),
            units = Units.Metric,
            onSelected = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun HourCardSelectedPreview() {
    AppPreview {
        HourCard(
            block = ForecastPreviewData.rainy(),
            tempUnit = "°C",
            isSelected = true,
            onClick = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun HourCardUnselectedPreview() {
    AppPreview {
        HourCard(
            block = ForecastPreviewData.sunny(),
            tempUnit = "°C",
            isSelected = false,
            onClick = {},
        )
    }
}
