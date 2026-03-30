package now.shouldigooutside.core.ui.preferences

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.forecast.AirQuality
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.preferences_aqi_description
import now.shouldigooutside.core.resources.preferences_aqi_title
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.AqiLevels
import now.shouldigooutside.core.ui.LocalTextStyle
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.IconButton
import now.shouldigooutside.core.ui.components.IconButtonVariant
import now.shouldigooutside.core.ui.components.Slider
import now.shouldigooutside.core.ui.components.SliderDefaults
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Info
import now.shouldigooutside.core.ui.icons.lucide.Waves
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

private val AqiSliderRange = 1f..10f

@Composable
public fun AqiRange(
    preferences: Preferences,
    update: (Preferences) -> Unit,
    modifier: Modifier = Modifier,
    onInfoClick: () -> Unit = {},
) {
    val levels = AqiLevels.forValue(preferences.maxAqi)
    val targetColors = levels.colors
    val bright by animateColorAsState(targetColors.bright)
    val container by animateColorAsState(targetColors.container)
    val contentColor by animateColorAsState(targetColors.containerContent)
    val sliderActive by animateColorAsState(targetColors.bright)
    val sliderInactive by animateColorAsState(targetColors.low)

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = container,
            contentColor = contentColor,
        ),
        modifier = modifier,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(bright)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = Res.string.preferences_aqi_title,
                        style = AppTheme.typography.h3,
                    )

                    Text(
                        text = Res.string.preferences_aqi_description,
                        autoSize = LocalTextStyle.current.autoSize(),
                        maxLines = 1,
                    )
                }

                IconButton(
                    onClick = onInfoClick,
                    variant = IconButtonVariant.Ghost,
                ) {
                    Icon(
                        icon = AppIcons.Lucide.Info,
                        contentDescription = Res.string.preferences_aqi_title.get(),
                    )
                }

                Icon(
                    icon = AppIcons.Lucide.Waves,
                    contentDescription = Res.string.preferences_aqi_title.get(),
                )
            }

            HorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp, end = 16.dp),
            ) {
                Text(
                    text = preferences.maxAqi.value.toString(),
                    style = AppTheme.typography.h1,
                )

                Crossfade(
                    targetState = levels,
                    modifier = Modifier.padding(start = 16.dp),
                ) { aqi ->
                    Column(
                        modifier = Modifier.padding(top = 4.dp),
                    ) {
                        Text(
                            text = aqi.title,
                            style = AppTheme.typography.h4,
                            maxLines = 1,
                            autoSize = AppTheme.typography.h4.autoSize(),
                        )
                        Text(
                            text = aqi.description,
                            maxLines = 3,
                            minLines = 3,
                            autoSize = AppTheme.typography.body3.autoSize(),
                            style = AppTheme.typography.body3,
                        )
                    }
                }
            }

            Slider(
                value = preferences.maxAqi.value.toFloat(),
                onValueChange = { update(preferences.copy(maxAqi = AirQuality(it.toInt()))) },
                valueRange = AqiSliderRange,
                steps = 8,
                colors = SliderDefaults.colors(
                    activeTrackColor = sliderActive,
                    inactiveTrackColor = sliderInactive,
                ),
                tickLabel = { SliderDefaults.TickLabel(it) },
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 16.dp),
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        var preferences by remember { mutableStateOf(Preferences.default) }
        AqiRange(
            preferences = preferences,
            update = { preferences = it },
        )
    }
}
