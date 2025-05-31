package app.sigot.forecast.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.DropdownMenu
import app.sigot.core.ui.components.DropdownMenuItem
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.mappers.rememberText
import app.sigot.forecast.ui.components.mappers.rememberInstant
import kotlinx.datetime.Instant

@Composable
internal fun Header(
    data: ForecastData?,
    period: ForecastPeriod,
    changePeriod: (ForecastPeriod) -> Unit,
    modifier: Modifier = Modifier,
    instant: Instant = data.rememberInstant(),
) {
    var showPeriodDropdown by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.wrapContentSize(Alignment.TopEnd),
    ) {
        HeaderText(
            instant = instant,
            period = period,
            onClick = { showPeriodDropdown = !showPeriodDropdown },
        )

        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopCenter)
                .align(Alignment.CenterHorizontally),
        ) {
            DropdownMenu(
                expanded = showPeriodDropdown,
                onDismissRequest = { showPeriodDropdown = false },
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
                            showPeriodDropdown = false
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
}
