package app.sigot.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalTextStyle
import app.sigot.core.ui.components.ChipDefaults.ChipIconHorizontalPadding
import app.sigot.core.ui.components.ChipDefaults.ChipIconSize
import app.sigot.core.ui.components.ChipDefaults.ChipRectShape
import app.sigot.core.ui.contentColorFor
import app.sigot.core.ui.foundation.ButtonElevation
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.PreviewIcon

@Composable
public fun Chip(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = ChipDefaults.contentPadding,
    shape: Shape = ChipRectShape,
    elevation: ButtonElevation? = null,
    colors: ChipColors = ChipDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit),
) {
    ChipComponent(
        modifier = modifier,
        enabled = enabled,
        selected = selected,
        style = ChipDefaults.filled(shape, colors, elevation),
        onClick = onClick,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label,
    )
}

@Composable
public fun ElevatedChip(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = ChipDefaults.contentPadding,
    shape: Shape = ChipRectShape,
    elevation: ButtonElevation = ChipDefaults.chipElevation(),
    colors: ChipColors = ChipDefaults.elevatedColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit),
) {
    ChipComponent(
        modifier = modifier,
        enabled = enabled,
        selected = selected,
        style = ChipDefaults.elevated(shape, colors, elevation),
        onClick = onClick,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = label,
    )
}

@Composable
private fun ChipComponent(
    modifier: Modifier,
    enabled: Boolean = true,
    selected: Boolean = false,
    style: ChipStyle,
    onClick: () -> Unit,
    contentPadding: PaddingValues = ChipDefaults.contentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable () -> Unit,
) {
    val containerColor = style.colors.containerColor(enabled, selected).value
    val contentColor = style.colors.contentColor(enabled, selected).value
    val borderColor = style.colors.borderColor(enabled, selected).value
    val borderStroke = BorderStroke(
        width = ChipDefaults.ChipOutlineHeight,
        color = borderColor,
    )

    val shadowElevation = style.elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp

    BrutalContainer(
        shape = style.shape,
        elevation = shadowElevation,
    ) {
        Surface(
            onClick = onClick,
            modifier = modifier.semantics { role = Role.Button },
            enabled = enabled,
            shape = style.shape,
            color = containerColor,
            contentColor = contentColor,
            border = borderStroke,
            shadowElevation = 0.dp,
            interactionSource = interactionSource,
        ) {
            DefaultChipComponent(
                modifier = Modifier.padding(contentPadding),
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                label = label,
            )
        }
    }
}

@Composable
private fun DefaultChipComponent(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit),
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon?.let { icon ->
            Box(
                modifier =
                    Modifier
                        .padding(end = ChipIconHorizontalPadding)
                        .requiredSize(ChipIconSize),
            ) {
                icon.invoke()
            }
        }

        CompositionLocalProvider(
            LocalTextStyle provides AppTheme.typography.label2,
        ) {
            label.invoke()
        }

        trailingIcon?.let { icon ->
            Box(
                modifier =
                    Modifier
                        .padding(start = ChipIconHorizontalPadding)
                        .requiredSize(ChipIconSize),
            ) {
                icon.invoke()
            }
        }
    }
}

public object ChipDefaults {
    private val ChipPaddingHorizontal = 8.dp
    private val ChipPaddingVertical = 6.dp
    internal val Elevation = BrutalElevationDefaults.Small
    public val ChipRectShape: Shape @Composable get() = AppTheme.shapes.extraSmall
    public val ChipOutlineHeight: Dp = BrutalDefaults.BorderWidth
    public val ChipIconHorizontalPadding: Dp = 6.dp
    public val ChipIconSize: Dp = 16.dp

    public val contentPadding: PaddingValues =
        PaddingValues(
            start = ChipPaddingHorizontal,
            end = ChipPaddingHorizontal,
            top = ChipPaddingVertical,
            bottom = ChipPaddingVertical,
        )

    @Composable
    public fun chipElevation(
        defaultElevation: Dp = Elevation.default,
        pressedElevation: Dp = Elevation.pressed,
        focusedElevation: Dp = Elevation.focused,
        hoveredElevation: Dp = Elevation.hovered,
        disabledElevation: Dp = Elevation.disabled,
    ): ButtonElevation =
        ButtonElevation(
            defaultElevation = defaultElevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            disabledElevation = disabledElevation,
        )

    @Composable
    public fun colors(
        containerColor: Color = AppTheme.colors.surface,
        contentColor: Color = AppTheme.colors.onSurface,
        outlineColor: Color = AppTheme.colors.outline,
        selectedContainerColor: Color = AppTheme.colors.primary,
        selectedContentColor: Color = AppTheme.colors.onPrimary,
        disabledContainerColor: Color = AppTheme.colors.disabled,
        disabledContentColor: Color = AppTheme.colors.onDisabled,
    ): ChipColors =
        ChipColors(
            containerColor = containerColor,
            contentColor = contentColor,
            outlineColor = outlineColor,
            selectedContainerColor = selectedContainerColor,
            selectedContentColor = selectedContentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun primaryColors(
        containerColor: Color = AppTheme.colors.primary,
        contentColor: Color = contentColorFor(containerColor),
        outlineColor: Color = AppTheme.colors.outline,
        selectedContainerColor: Color = AppTheme.colors.primary,
        selectedContentColor: Color = contentColorFor(selectedContainerColor),
        disabledContainerColor: Color = AppTheme.colors.disabled,
        disabledContentColor: Color = AppTheme.colors.onDisabled,
    ): ChipColors =
        ChipColors(
            containerColor = containerColor,
            contentColor = contentColor,
            outlineColor = outlineColor,
            selectedContainerColor = selectedContainerColor,
            selectedContentColor = selectedContentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun secondaryColors(
        containerColor: Color = AppTheme.colors.secondary,
        contentColor: Color = contentColorFor(containerColor),
        outlineColor: Color = AppTheme.colors.outline,
        selectedContainerColor: Color = AppTheme.colors.secondary,
        selectedContentColor: Color = contentColorFor(selectedContainerColor),
        disabledContainerColor: Color = AppTheme.colors.disabled,
        disabledContentColor: Color = AppTheme.colors.onDisabled,
    ): ChipColors =
        ChipColors(
            containerColor = containerColor,
            contentColor = contentColor,
            outlineColor = outlineColor,
            selectedContainerColor = selectedContainerColor,
            selectedContentColor = selectedContentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun tertiaryColors(
        containerColor: Color = AppTheme.colors.tertiary,
        contentColor: Color = contentColorFor(containerColor),
        outlineColor: Color = AppTheme.colors.outline,
        selectedContainerColor: Color = AppTheme.colors.tertiary,
        selectedContentColor: Color = contentColorFor(selectedContainerColor),
        disabledContainerColor: Color = AppTheme.colors.disabled,
        disabledContentColor: Color = AppTheme.colors.onDisabled,
    ): ChipColors =
        ChipColors(
            containerColor = containerColor,
            contentColor = contentColor,
            outlineColor = outlineColor,
            selectedContainerColor = selectedContainerColor,
            selectedContentColor = selectedContentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun filled(
        shape: Shape,
        colors: ChipColors = colors(),
        elevation: ButtonElevation? = null,
    ): ChipStyle =
        ChipStyle(
            colors = colors,
            shape = shape,
            elevation = elevation,
            contentPadding = contentPadding,
        )

    @Composable
    public fun elevatedColors(
        containerColor: Color = AppTheme.colors.surface,
        contentColor: Color = AppTheme.colors.onSurface,
        outlineColor: Color = AppTheme.colors.outline,
        selectedContainerColor: Color = AppTheme.colors.primary,
        selectedContentColor: Color = AppTheme.colors.onPrimary,
        disabledContainerColor: Color = AppTheme.colors.disabled,
        disabledContentColor: Color = AppTheme.colors.onDisabled,
    ): ChipColors =
        ChipColors(
            containerColor = containerColor,
            contentColor = contentColor,
            outlineColor = outlineColor,
            selectedContainerColor = selectedContainerColor,
            selectedContentColor = selectedContentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun elevated(
        shape: Shape,
        colors: ChipColors = elevatedColors(),
        elevation: ButtonElevation = chipElevation(),
    ): ChipStyle =
        ChipStyle(
            colors = colors,
            shape = shape,
            elevation = elevation,
            contentPadding = contentPadding,
        )
}

@Immutable
public data class ChipColors(
    val containerColor: Color,
    val contentColor: Color,
    val outlineColor: Color,
    val selectedContainerColor: Color,
    val selectedOutlineColor: Color = outlineColor,
    val selectedContentColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
    val disabledOutlineColor: Color = outlineColor,
) {
    @Composable
    public fun containerColor(
        enabled: Boolean,
        selected: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            newValue =
                when {
                    !enabled -> disabledContainerColor
                    selected -> selectedContainerColor
                    else -> containerColor
                },
        )

    @Composable
    public fun contentColor(
        enabled: Boolean,
        selected: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            newValue =
                when {
                    !enabled -> disabledContentColor
                    selected -> selectedContentColor
                    else -> contentColor
                },
        )

    @Composable
    public fun borderColor(
        enabled: Boolean,
        selected: Boolean,
    ): State<Color> =
        rememberUpdatedState(
            newValue =
                when {
                    !enabled -> disabledOutlineColor
                    selected -> selectedOutlineColor
                    else -> outlineColor
                },
        )
}

@Immutable
public data class ChipStyle(
    public val colors: ChipColors,
    public val shape: Shape,
    public val elevation: ButtonElevation? = null,
    public val contentPadding: PaddingValues,
)

@Composable
private fun ChipPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(
                leadingIcon = { Icon(PreviewIcon) },
            ) {
                Text("Chip")
            }

            Chip(
                leadingIcon = { Icon(PreviewIcon) },
                enabled = false,
            ) {
                Text("Chip")
            }

            Chip(
                trailingIcon = { Icon(PreviewIcon) },
            ) {
                Text("Chip")
            }

            Chip(
                trailingIcon = { Icon(PreviewIcon) },
                enabled = false,
            ) {
                Text("Chip", style = AppTheme.typography.label3)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElevatedChip(
                leadingIcon = { Icon(PreviewIcon) },
            ) {
                Text("Chip")
            }

            ElevatedChip(
                leadingIcon = { Icon(PreviewIcon) },
                enabled = false,
            ) {
                Text("Chip")
            }

            ElevatedChip(
                trailingIcon = { Icon(PreviewIcon) },
            ) {
                Text("Chip")
            }

            ElevatedChip(
                trailingIcon = { Icon(PreviewIcon) },
                enabled = false,
            ) {
                Text("Chip", style = AppTheme.typography.label3)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.primaryColors(),
            ) {
                Text("Chip")
            }

            Chip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.primaryColors(),
                enabled = false,
            ) {
                Text("Chip")
            }

            Chip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.primaryColors(),
            ) {
                Text("Chip")
            }

            Chip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.primaryColors(),
                enabled = false,
            ) {
                Text("Chip", style = AppTheme.typography.label3)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElevatedChip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.primaryColors(),
            ) {
                Text("Primary")
            }

            ElevatedChip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.primaryColors(),
                enabled = false,
            ) {
                Text("Primary")
            }

            ElevatedChip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.primaryColors(),
            ) {
                Text("Primary")
            }

            ElevatedChip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.primaryColors(),
                enabled = false,
            ) {
                Text("Primary", style = AppTheme.typography.label3)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.secondaryColors(),
            ) {
                Text("Chip")
            }

            Chip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.secondaryColors(),
                enabled = false,
            ) {
                Text("Chip")
            }

            Chip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.secondaryColors(),
            ) {
                Text("Chip")
            }

            Chip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.secondaryColors(),
                enabled = false,
            ) {
                Text("Chip", style = AppTheme.typography.label3)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElevatedChip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.secondaryColors(),
            ) {
                Text("Primary")
            }

            ElevatedChip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.secondaryColors(),
                enabled = false,
            ) {
                Text("Primary")
            }

            ElevatedChip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.secondaryColors(),
            ) {
                Text("Primary")
            }

            ElevatedChip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.secondaryColors(),
                enabled = false,
            ) {
                Text("Primary", style = AppTheme.typography.label3)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.tertiaryColors(),
            ) {
                Text("Chip")
            }

            Chip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.tertiaryColors(),
                enabled = false,
            ) {
                Text("Chip")
            }

            Chip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.tertiaryColors(),
            ) {
                Text("Chip")
            }

            Chip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.tertiaryColors(),
                enabled = false,
            ) {
                Text("Chip", style = AppTheme.typography.label3)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ElevatedChip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.tertiaryColors(),
            ) {
                Text("Primary")
            }

            ElevatedChip(
                leadingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.tertiaryColors(),
                enabled = false,
            ) {
                Text("Primary")
            }

            ElevatedChip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.tertiaryColors(),
            ) {
                Text("Primary")
            }

            ElevatedChip(
                trailingIcon = { Icon(PreviewIcon) },
                colors = ChipDefaults.tertiaryColors(),
                enabled = false,
            ) {
                Text("Primary", style = AppTheme.typography.label3)
            }
        }
    }
}

@Preview
@Composable
internal fun ChipLightPreview() {
    AppPreview { ChipPreview() }
}

@Preview
@Composable
internal fun ChipDarkPreview() {
    AppPreview(isDarkTheme = true) { ChipPreview() }
}
