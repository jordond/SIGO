package now.shouldigooutside.forecast.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import now.shouldigooutside.core.model.forecast.ForecastPeriod
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.forecast_title_in
import now.shouldigooutside.core.resources.forecast_title_outside
import now.shouldigooutside.core.resources.forecast_title_prefix
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.activities.rememberDisplayName
import now.shouldigooutside.core.ui.asDisplay
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.DropdownMenu
import now.shouldigooutside.core.ui.components.DropdownMenuItem
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
internal fun Header(
    period: ForecastPeriod,
    changePeriod: (ForecastPeriod) -> Unit,
    selectedActivity: Activity,
    activities: PersistentList<Activity>,
    changeActivity: (Activity) -> Unit,
    location: Location?,
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier,
    instant: Instant = remember(period) { Clock.System.now() },
) {
    var showPeriodDropdown by remember { mutableStateOf(false) }
    var showActivityDropdown by remember { mutableStateOf(false) }
    val hasMultipleActivities = activities.size > 1

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = Res.string.forecast_title_prefix.get(),
            style = AppTheme.typography.h1,
            fontStyle = FontStyle.Italic,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        ) {
            if (hasMultipleActivities) {
                Box {
                    Button(
                        onClick = { showActivityDropdown = !showActivityDropdown },
                        shape = AppTheme.shapes.extraSmall,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        variant = ButtonVariant.PrimaryElevated,
                    ) {
                        Text(
                            text = selectedActivity.rememberDisplayName(),
                            style = AppTheme.typography.h2,
                        )
                    }
                    DropdownMenu(
                        expanded = showActivityDropdown,
                        onDismissRequest = { showActivityDropdown = false },
                    ) {
                        val otherActivities = remember(activities, selectedActivity) {
                            activities.filter { it != selectedActivity }
                        }
                        otherActivities.forEachIndexed { index, activity ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = activity.rememberDisplayName(),
                                        style = AppTheme.typography.h3,
                                    )
                                },
                                onClick = {
                                    showActivityDropdown = false
                                    changeActivity(activity)
                                },
                            )
                            if (index != otherActivities.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
            } else {
                Text(
                    text = Res.string.forecast_title_outside.get(),
                    style = AppTheme.typography.h1,
                    fontStyle = FontStyle.Italic,
                )
            }

            PeriodSelector(
                period = period,
                instant = instant,
                changePeriod = changePeriod,
            )
        }

        if (location != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp),
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
            period = ForecastPeriod.Today,
            changePeriod = {},
            selectedActivity = Activity.General,
            activities = persistentListOf(Activity.General),
            changeActivity = {},
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
            period = ForecastPeriod.Today,
            changePeriod = {},
            selectedActivity = Activity.General,
            activities = persistentListOf(Activity.General),
            changeActivity = {},
            location = Location(0.0, 0.0, "Sample"),
            onLocationClick = {},
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun ActivityPreview() {
    AppPreview {
        Header(
            period = ForecastPeriod.Today,
            changePeriod = {},
            selectedActivity = Activity.Running,
            activities = Activity.all.toPersistentList(),
            changeActivity = {},
            location = Location(0.0, 0.0, "Toronto"),
            onLocationClick = {},
        )
    }
}
