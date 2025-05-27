package app.sigot.settings.ui.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.sigot.core.resources.Res
import app.sigot.core.resources.open
import app.sigot.core.resources.settings_experience_haptics
import app.sigot.core.resources.settings_experience_haptics_desc
import app.sigot.core.resources.settings_experience_preferences
import app.sigot.core.resources.settings_experience_preferences_desc
import app.sigot.core.resources.settings_experience_title
import app.sigot.core.resources.settings_experience_units
import app.sigot.core.resources.settings_experience_units_desc
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Switch
import app.sigot.core.ui.components.SwitchDefaults
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.ArrowRight
import app.sigot.core.ui.icons.lucide.Ruler
import app.sigot.core.ui.icons.lucide.Vibrate
import app.sigot.core.ui.icons.lucide.VibrateOff
import app.sigot.core.ui.icons.lucide.Wrench
import app.sigot.core.ui.preview.AppPreview
import app.sigot.settings.ui.components.SettingsCard
import app.sigot.settings.ui.components.SettingsTextRow
import app.sigot.settings.ui.components.TrailingBorderedIcon
import org.jetbrains.compose.ui.tooling.preview.Preview

private val trailingContent = @Composable {
    TrailingBorderedIcon(
        icon = AppIcons.Lucide.ArrowRight,
        contentDescription = Res.string.open,
        color = AppTheme.colors.primary,
    )
}

@Composable
internal fun ExperienceSection(
    haptics: Boolean,
    toggleHaptics: () -> Unit,
    unitsClick: () -> Unit,
    preferencesClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Color = AppTheme.colors.primary,
    secondary: Color = AppTheme.colors.secondary,
) {
    SettingsCard(
        text = Res.string.settings_experience_title,
        colors = CardDefaults.fromColor(primary),
    ) {
        Item {
            val icon = remember(haptics) {
                if (haptics) AppIcons.Lucide.Vibrate else AppIcons.Lucide.VibrateOff
            }
            SettingsTextRow(
                text = Res.string.settings_experience_haptics,
                description = Res.string.settings_experience_haptics_desc,
                icon = icon,
                onClick = { toggleHaptics() },
                trailingContent = {
                    Switch(
                        checked = haptics,
                        onCheckedChange = { toggleHaptics() },
                        colors = SwitchDefaults.colors(checkedTrackColor = secondary),
                    )
                },
            )
        }

        Item {
            SettingsTextRow(
                text = Res.string.settings_experience_units,
                description = Res.string.settings_experience_units_desc,
                icon = AppIcons.Lucide.Ruler,
                onClick = unitsClick,
                trailingContent = trailingContent,
            )
        }

        Item(isLast = true) {
            SettingsTextRow(
                text = Res.string.settings_experience_preferences,
                description = Res.string.settings_experience_preferences_desc,
                icon = AppIcons.Lucide.Wrench,
                onClick = preferencesClick,
                trailingContent = trailingContent,
            )
        }
    }
}

@Preview
@Composable
private fun ExperienceSectionPreview() {
    var haptics by remember { mutableStateOf(false) }
    AppPreview {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            ExperienceSection(
                haptics = haptics,
                toggleHaptics = { haptics = !haptics },
                unitsClick = {},
                preferencesClick = {},
            )
        }
    }
}
