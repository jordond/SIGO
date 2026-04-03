package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.DropdownMenu
import now.shouldigooutside.core.ui.components.DropdownMenuItem
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.mappers.rememberText
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
internal fun PeriodSelector(
    period: ForecastPeriod,
    changePeriod: (ForecastPeriod) -> Unit,
    modifier: Modifier = Modifier,
    instant: Instant = remember(period) { Clock.System.now() },
) {
    var show by remember { mutableStateOf(false) }
    PeriodSelector(
        show = show,
        update = { show = it },
        period = period,
        changePeriod = changePeriod,
        modifier = modifier,
        instant = instant,
    )
}

@Composable
internal fun PeriodSelector(
    show: Boolean,
    update: (Boolean) -> Unit,
    period: ForecastPeriod,
    changePeriod: (ForecastPeriod) -> Unit,
    modifier: Modifier = Modifier,
    instant: Instant = remember(period) { Clock.System.now() },
) {
    Box(
        modifier = modifier,
    ) {
        Button(
            onClick = { update(!show) },
            shape = AppTheme.shapes.extraSmall,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            variant = ButtonVariant.PrimaryElevated,
        ) {
            Text(
                text = period.rememberText(instant),
                style = AppTheme.typography.h2,
            )
        }
        DropdownMenu(
            expanded = show,
            onDismissRequest = { update(false) },
        ) {
            val entries = remember(period) {
                ForecastPeriod.entries - period
            }
            entries.forEachIndexed { index, entry ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = entry.rememberText(instant),
                            style = AppTheme.typography.h3,
                        )
                    },
                    onClick = {
                        update(false)
                        changePeriod(entry)
                    },
                )
                if (index != entries.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}
