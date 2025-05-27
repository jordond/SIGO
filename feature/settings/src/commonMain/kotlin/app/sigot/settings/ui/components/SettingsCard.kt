package app.sigot.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sigot.core.resources.Res
import app.sigot.core.resources.settings
import app.sigot.core.resources.settings_about_privacy
import app.sigot.core.resources.settings_about_website
import app.sigot.core.resources.settings_experience_haptics
import app.sigot.core.resources.settings_experience_haptics_desc
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.components.BrutalDefaults
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.IconButton
import app.sigot.core.ui.components.IconButtonVariant
import app.sigot.core.ui.components.Switch
import app.sigot.core.ui.components.SwitchDefaults
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.card.CardColors
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Droplet
import app.sigot.core.ui.icons.lucide.Share
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val itemScope = object : SettingsCardItemScope {}

internal class SettingsCardScope(
    delegate: ColumnScope,
) : ColumnScope by delegate {
    @Composable
    fun Item(
        isLast: Boolean = false,
        content: @Composable SettingsCardItemScope.() -> Unit,
    ) {
        Column {
            content(itemScope)
            if (!isLast) {
                HorizontalDivider()
            }
        }
    }
}

internal interface SettingsCardItemScope

@Composable
internal fun SettingsHeader(
    text: StringResource,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.get(),
        style = AppTheme.typography.h2,
        modifier = modifier,
    )
}

@Composable
internal fun SettingsCard(
    text: StringResource,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    content: @Composable SettingsCardScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SettingsHeader(
            text = text,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        ElevatedCard(
            colors = colors,
        ) {
            Column {
                val scope = remember { SettingsCardScope(this) }
                content(scope)
            }
        }
    }
}

@Composable
internal fun SettingsTextRow(
    text: StringResource,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    style: TextStyle = AppTheme.typography.h4.copy(fontSize = 20.sp),
) {
    val textValue = text.get()
    SettingsRow(
        onClick = onClick,
        icon = icon,
        contentDescription = textValue,
        trailingContent = trailingContent,
        modifier = modifier,
    ) {
        Text(
            text = textValue,
            style = style,
        )
    }
}

@Composable
internal fun SettingsTextRow(
    text: StringResource,
    description: StringResource,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    style: TextStyle = AppTheme.typography.h4.copy(fontSize = 20.sp),
    descriptionStyle: TextStyle = AppTheme.typography.body2.copy(fontStyle = FontStyle.Italic),
) {
    val textValue = text.get()
    SettingsRow(
        onClick = onClick,
        icon = icon,
        contentDescription = textValue,
        trailingContent = trailingContent,
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = textValue,
                style = style,
            )

            Text(
                text = description,
                style = descriptionStyle,
                maxLines = 2,
                autoSize = TextAutoSize.StepBased(maxFontSize = descriptionStyle.fontSize),
            )
        }
    }
}

@Composable
internal fun TrailingBorderedIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = LocalContainerColor.current,
    contentDescription: StringResource? = null,
) {
    Box(
        modifier = modifier
            .background(color, AppTheme.shapes.small)
            .border(BrutalDefaults.Border, AppTheme.shapes.small)
            .padding(8.dp),
    ) {
        Icon(
            icon = icon,
            contentDescription = contentDescription?.get(),
        )
    }
}

@Composable
internal fun SettingsRow(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .heightIn(min = 64.dp)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            ).padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        if (icon != null) {
            Icon(
                icon = icon,
                contentDescription = contentDescription,
                modifier = Modifier.padding(end = 16.dp),
            )
        }

        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
        ) {
            content()
        }

        if (trailingContent != null) {
            trailingContent()
        }
    }
}

@Preview
@Composable
private fun SettingsCardPreview() {
    AppPreview {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            SettingsCard(
                text = Res.string.settings,
                colors = CardDefaults.primaryColors,
            ) {
                Item {
                    SettingsTextRow(
                        text = Res.string.settings_about_privacy,
                        onClick = {},
                        icon = AppIcons.Lucide.Share,
                    )
                }

                Item {
                    SettingsTextRow(
                        text = Res.string.settings_about_privacy,
                        onClick = {},
                    )
                }

                Item {
                    SettingsTextRow(
                        text = Res.string.settings_about_website,
                        onClick = {},
                        trailingContent = {
                            IconButton(onClick = {}, variant = IconButtonVariant.SecondaryElevated) {
                                Icon(icon = AppIcons.Lucide.Droplet)
                            }
                        },
                    )
                }

                var checked by remember { mutableStateOf(false) }
                SettingsTextRow(
                    text = Res.string.settings_experience_haptics,
                    description = Res.string.settings_experience_haptics_desc,
                    onClick = { checked = !checked },
                    trailingContent = {
                        Switch(
                            checked = checked,
                            onCheckedChange = { checked = it },
                            colors = SwitchDefaults.secondaryColors(),
                        )
                    },
                )
            }
        }
    }
}
