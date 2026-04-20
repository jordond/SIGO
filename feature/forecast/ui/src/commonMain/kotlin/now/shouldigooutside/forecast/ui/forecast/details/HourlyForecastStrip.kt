package now.shouldigooutside.forecast.ui.forecast.details

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import now.shouldigooutside.core.model.forecast.ForecastBlock
import now.shouldigooutside.core.model.score.ScoreResult
import now.shouldigooutside.core.model.units.Units
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_period_now
import now.shouldigooutside.core.resources.forecast_period_tomorrow
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.contentColorFor
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Droplet
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.ktx.scrollToBottom
import now.shouldigooutside.core.ui.ktx.scrollToTop
import now.shouldigooutside.core.ui.ktx.text
import now.shouldigooutside.core.ui.mappers.units.rememberUnit
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.ForecastPreviewData
import now.shouldigooutside.forecast.ui.components.mappers.color
import kotlin.time.Clock
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
    scoreFor: (ForecastBlock) -> ScoreResult? = { null },
) {
    val tempUnit = units.temperature.rememberUnit()

    val initialFirst = remember {
        hours.indexOfFirst { it == (selected ?: now) }.takeIf { it != -1 } ?: 0
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialFirst)

    LaunchedEffect(selected) {
        when (selected) {
            null, now -> {
                listState.scrollToTop()
            }
            tomorrow -> {
                listState.scrollToBottom()
            }
            else -> {
                hours.indexOf(selected).takeIf { it != -1 }?.let { index ->
                    listState.animateScrollToItem(index + 1)
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
                isSelected = now == selected,
                score = scoreFor(now),
                onClick = { onSelected(now) },
            )
        }

        items(hours, key = { it.instant.toString() }) { hour ->
            val isSelected = selected == hour
            val dateTime = remember(hour.instant) {
                val hour = hour.instant.toLocalDateTime(TimeZone.currentSystemDefault()).hour
                LocalTime(hour = hour, minute = 0)
            }

            HourCard(
                text = dateTime.text(),
                block = hour,
                tempUnit = tempUnit,
                isSelected = isSelected,
                score = scoreFor(hour),
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
                    score = scoreFor(tomorrow),
                    onClick = { onSelected(tomorrow) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@Composable
internal fun HourCard(
    text: String,
    block: ForecastBlock,
    tempUnit: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    score: ScoreResult? = null,
) {
    val scoreColor = score?.color()?.container
    val containerColor by animateColorAsState(
        targetValue = when {
            scoreColor != null -> scoreColor
            else -> AppTheme.colors.surface
        },
        label = "HourCardContainer",
    )
    val contentColor by animateColorAsState(
        targetValue = AppTheme.colors.contentColorFor(containerColor),
        label = "HourCardContent",
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 4.dp else 2.dp,
        label = "HourCardElevation",
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) CardDefaults.ElevatedBorderWidth else 0.dp,
        label = "HourCardBorder",
    )
    val border = CardDefaults.cardBorder(width = borderWidth)

    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation, pressedElevation = elevation),
        modifier = modifier.width(100.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
        ) {
            Text(
                text = text,
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
    val now = Clock.System
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
            text = "Now",
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
            text = "Foo",
            block = ForecastPreviewData.sunny(),
            tempUnit = "°C",
            isSelected = false,
            onClick = {},
        )
    }
}
