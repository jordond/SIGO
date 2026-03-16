package now.shouldigooutside.forecast.ui.components

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
import now.shouldigooutside.core.model.ForecastData
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_title_in
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.asDisplay
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.DropdownMenu
import now.shouldigooutside.core.ui.components.DropdownMenuItem
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.rememberText
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.forecast.ui.components.mappers.rememberInstant
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
