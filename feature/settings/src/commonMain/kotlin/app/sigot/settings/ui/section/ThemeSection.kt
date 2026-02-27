package app.sigot.settings.ui.section

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.model.ui.ThemeMode
import app.sigot.core.model.units.UnitPreset
import app.sigot.core.resources.Res
import app.sigot.core.resources.settings_theme_dark
import app.sigot.core.resources.settings_theme_light
import app.sigot.core.resources.settings_theme_system
import app.sigot.core.resources.settings_theme_title
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalHaptics
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.SegmentedButton
import app.sigot.core.ui.components.SegmentedButtonDefaults
import app.sigot.core.ui.components.SegmentedButtonDefaults.itemShape
import app.sigot.core.ui.components.SingleChoiceSegmentedButtonRow
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.contentColorFor
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Moon
import app.sigot.core.ui.icons.lucide.Smartphone
import app.sigot.core.ui.icons.lucide.Sun
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import app.sigot.settings.ui.components.SettingsHeader

@Composable
internal fun ThemeSection(
    selected: ThemeMode,
    updateTheme: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    primary: Color = AppTheme.colors.success,
    secondary: Color = AppTheme.colors.primary,
) {
    val haptics = LocalHaptics.current
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SettingsHeader(Res.string.settings_theme_title)

        SingleChoiceSegmentedButtonRow {
            ThemeMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = primary,
                        activeContentColor = contentColorFor(primary),
                        inactiveContainerColor = secondary,
                        inactiveContentColor = contentColorFor(secondary),
                    ),
                    shape = itemShape(
                        index = index,
                        count = UnitPreset.entries.size,
                    ),
                    onClick = {
                        haptics.click()
                        updateTheme(mode)
                    },
                    selected = selected == mode,
                    label = {
                        val text = remember(mode) {
                            when (mode) {
                                ThemeMode.Light -> Res.string.settings_theme_light
                                ThemeMode.Dark -> Res.string.settings_theme_dark
                                ThemeMode.System -> Res.string.settings_theme_system
                            }
                        }.get()

                        val icon = remember(mode) {
                            when (mode) {
                                ThemeMode.Light -> AppIcons.Lucide.Sun
                                ThemeMode.Dark -> AppIcons.Lucide.Moon
                                ThemeMode.System -> AppIcons.Lucide.Smartphone
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(icon = icon, contentDescription = text)
                            Text(text)
                        }
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun ThemeSectionPreview() {
    var selected by remember { mutableStateOf(ThemeMode.System) }
    val systemDarkMode = isSystemInDarkTheme()
    val isDarkTheme = remember(systemDarkMode, selected) {
        when (selected) {
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
            ThemeMode.System -> systemDarkMode
        }
    }

    AppPreview(isDarkTheme = isDarkTheme) {
        Box(
            modifier = Modifier.padding(16.dp),
        ) {
            ThemeSection(
                selected = selected,
                updateTheme = { selected = it },
            )
        }
    }
}
