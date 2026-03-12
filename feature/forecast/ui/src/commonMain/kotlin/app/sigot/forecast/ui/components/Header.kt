package app.sigot.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.ForecastData
import app.sigot.core.model.forecast.ForecastPeriod
import app.sigot.core.model.location.Location
import app.sigot.core.resources.Res
import app.sigot.core.resources.forecast_title_in
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.asDisplay
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.DropdownMenu
import app.sigot.core.ui.components.DropdownMenuItem
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.autoSize
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.mappers.rememberText
import app.sigot.core.ui.preview.AppPreview
import app.sigot.forecast.ui.components.mappers.rememberInstant
import kotlin.time.Instant

@Composable
internal fun Header(
    data: ForecastData?,
    period: ForecastPeriod,
    changePeriod: (ForecastPeriod) -> Unit,
    location: Location?,
    onLocationClick: () -> Unit,
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

        if (location != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp),
            ) {
                Text(
                    text = Res.string.forecast_title_in.get(),
                    style = AppTheme.typography.h2,
                )
                Button(
                    onClick = onLocationClick,
                    shape = AppTheme.shapes.extraSmall,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    variant = ButtonVariant.SecondaryElevated,
                ) {
                    Text(
                        text = location.name,
                        style = AppTheme.typography.h3.asDisplay,
                        autoSize = AppTheme.typography.h3.autoSize(),
                        maxLines = 1,
                    )
                }
            }
        }

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

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        Header(
            data = null,
            period = ForecastPeriod.Today,
            changePeriod = {},
            location = null,
            onLocationClick = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun LocationPreview() {
    AppPreview {
        Header(
            data = null,
            period = ForecastPeriod.Today,
            changePeriod = {},
            location = Location(0.0, 0.0, "Sample"),
            onLocationClick = {},
        )
    }
}
