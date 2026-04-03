package now.shouldigooutside.settings.ui.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.disable
import now.shouldigooutside.core.resources.open
import now.shouldigooutside.core.resources.settings_experience_24_hour_format
import now.shouldigooutside.core.resources.settings_experience_24_hour_format_desc
import now.shouldigooutside.core.resources.settings_experience_disable_activities
import now.shouldigooutside.core.resources.settings_experience_disable_activities_warning
import now.shouldigooutside.core.resources.settings_experience_enable_activities
import now.shouldigooutside.core.resources.settings_experience_enable_activities_desc
import now.shouldigooutside.core.resources.settings_experience_haptics
import now.shouldigooutside.core.resources.settings_experience_haptics_desc
import now.shouldigooutside.core.resources.settings_experience_include_air_quality
import now.shouldigooutside.core.resources.settings_experience_include_air_quality_desc
import now.shouldigooutside.core.resources.settings_experience_remember_activity
import now.shouldigooutside.core.resources.settings_experience_remember_activity_desc
import now.shouldigooutside.core.resources.settings_experience_title
import now.shouldigooutside.core.resources.settings_experience_units
import now.shouldigooutside.core.resources.settings_experience_units_desc
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.AlertDialog
import now.shouldigooutside.core.ui.components.Switch
import now.shouldigooutside.core.ui.components.SwitchDefaults
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.ArrowRight
import now.shouldigooutside.core.ui.icons.lucide.Hourglass
import now.shouldigooutside.core.ui.icons.lucide.Ruler
import now.shouldigooutside.core.ui.icons.lucide.Vibrate
import now.shouldigooutside.core.ui.icons.lucide.VibrateOff
import now.shouldigooutside.core.ui.icons.lucide.Waves
import now.shouldigooutside.core.ui.icons.phosphor.Hike
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.settings.ui.components.SettingsCard
import now.shouldigooutside.settings.ui.components.SettingsTextRow
import now.shouldigooutside.settings.ui.components.TrailingBorderedIcon

private val trailingContent = @Composable {
    TrailingBorderedIcon(
        icon = AppIcons.Lucide.ArrowRight,
        contentDescription = Res.string.open,
        color = AppTheme.colors.primary,
    )
}

@Composable
internal fun ExperienceSection(
    settings: Settings,
    toggleHaptics: () -> Unit,
    toggle24HourFormat: () -> Unit,
    toggleAirQuality: () -> Unit,
    toggleActivities: () -> Unit,
    toggleRememberActivity: () -> Unit,
    unitsClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Color = AppTheme.colors.primary,
    secondary: Color = AppTheme.colors.secondary,
) {
    var showDisableActivitiesDialog by remember { mutableStateOf(false) }

    SettingsCard(
        text = Res.string.settings_experience_title,
        colors = CardDefaults.fromColor(primary),
    ) {
        Item {
            val icon = remember(settings.enableHaptics) {
                if (settings.enableHaptics) AppIcons.Lucide.Vibrate else AppIcons.Lucide.VibrateOff
            }
            SettingsTextRow(
                text = Res.string.settings_experience_haptics,
                description = Res.string.settings_experience_haptics_desc,
                icon = icon,
                onClick = { toggleHaptics() },
                trailingContent = {
                    Switch(
                        checked = settings.enableHaptics,
                        onCheckedChange = { toggleHaptics() },
                        colors = SwitchDefaults.colors(checkedTrackColor = secondary),
                    )
                },
            )
        }

        Item {
            SettingsTextRow(
                text = Res.string.settings_experience_24_hour_format,
                description = Res.string.settings_experience_24_hour_format_desc,
                icon = AppIcons.Lucide.Hourglass,
                onClick = toggle24HourFormat,
                trailingContent = {
                    Switch(
                        checked = settings.use24HourFormat,
                        onCheckedChange = { toggle24HourFormat() },
                        colors = SwitchDefaults.colors(checkedTrackColor = secondary),
                    )
                },
            )
        }

        Item {
            SettingsTextRow(
                text = Res.string.settings_experience_include_air_quality,
                description = Res.string.settings_experience_include_air_quality_desc,
                icon = AppIcons.Lucide.Waves,
                onClick = toggleAirQuality,
                trailingContent = {
                    Switch(
                        checked = settings.includeAirQuality,
                        onCheckedChange = { toggleAirQuality() },
                        colors = SwitchDefaults.colors(checkedTrackColor = secondary),
                    )
                },
            )
        }

        Item {
            SettingsTextRow(
                text = Res.string.settings_experience_enable_activities,
                description = Res.string.settings_experience_enable_activities_desc,
                icon = AppIcons.Phosphor.Hike,
                onClick = toggleActivities,
                trailingContent = {
                    Switch(
                        checked = settings.enableActivities,
                        onCheckedChange = {
                            if (settings.enableActivities) {
                                showDisableActivitiesDialog = true
                            } else {
                                toggleActivities()
                            }
                        },
                        colors = SwitchDefaults.colors(checkedTrackColor = secondary),
                    )
                },
            )
        }

        Item {
            SettingsTextRow(
                text = Res.string.settings_experience_remember_activity,
                description = Res.string.settings_experience_remember_activity_desc,
                icon = AppIcons.Lucide.Hourglass,
                onClick = toggleRememberActivity,
                trailingContent = {
                    Switch(
                        checked = settings.rememberActivity,
                        onCheckedChange = { toggleRememberActivity() },
                        colors = SwitchDefaults.colors(checkedTrackColor = secondary),
                    )
                },
            )
        }

        Item(isLast = true) {
            SettingsTextRow(
                text = Res.string.settings_experience_units,
                description = Res.string.settings_experience_units_desc,
                icon = AppIcons.Lucide.Ruler,
                onClick = unitsClick,
                trailingContent = trailingContent,
            )
        }
    }

    if (showDisableActivitiesDialog) {
        AlertDialog(
            title = Res.string.settings_experience_disable_activities.get(),
            text = Res.string.settings_experience_disable_activities_warning.get(),
            confirmButtonText = Res.string.disable.get(),
            onDismissRequest = { showDisableActivitiesDialog = false },
            onConfirmClick = {
                toggleActivities()
                showDisableActivitiesDialog = false
            },
        )
    }
}

@Preview
@Composable
private fun ExperienceSectionPreview() {
    var settings by remember { mutableStateOf(Settings()) }
    AppPreview {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            ExperienceSection(
                settings = settings,
                toggleHaptics = { settings = settings.copy(enableHaptics = !settings.enableHaptics) },
                toggle24HourFormat = {
                    settings = settings.copy(use24HourFormat = !settings.use24HourFormat)
                },
                unitsClick = {},
                toggleActivities = {},
                toggleAirQuality = {},
                toggleRememberActivity = {},
            )
        }
    }
}
