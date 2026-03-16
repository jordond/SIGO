package now.shouldigooutside.settings.ui.section

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
import now.shouldigooutside.core.model.ui.ThemeMode
import now.shouldigooutside.core.model.units.UnitPreset
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.settings_theme_dark
import now.shouldigooutside.core.resources.settings_theme_light
import now.shouldigooutside.core.resources.settings_theme_system
import now.shouldigooutside.core.resources.settings_theme_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalHaptics
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.SegmentedButton
import now.shouldigooutside.core.ui.components.SegmentedButtonDefaults
import now.shouldigooutside.core.ui.components.SegmentedButtonDefaults.itemShape
import now.shouldigooutside.core.ui.components.SingleChoiceSegmentedButtonRow
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.contentColorFor
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Moon
import now.shouldigooutside.core.ui.icons.lucide.Smartphone
import now.shouldigooutside.core.ui.icons.lucide.Sun
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.settings.ui.components.SettingsHeader

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
