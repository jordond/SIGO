package app.sigot.core.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.LocalContentColor
import app.sigot.core.ui.components.BrutalDefaults.DisabledAlpha
import app.sigot.core.ui.components.progressindicators.CircularProgressIndicator
import app.sigot.core.ui.contentColorFor
import app.sigot.core.ui.foundation.ButtonElevation
import app.sigot.core.ui.foundation.ripple
import app.sigot.core.ui.ktx.disabled
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun IconButton(
    style: IconButtonStyle,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = IconButtonDefaults.ButtonSquareShape,
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = IconButtonDefaults.ButtonPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    IconButtonComponent(
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        style = style,
        onClick = onClick,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
public fun IconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    variant: IconButtonVariant = IconButtonVariant.Primary,
    shape: Shape = IconButtonDefaults.ButtonSquareShape,
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = IconButtonDefaults.ButtonPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val style = IconButtonDefaults.styleFor(variant, shape)

    IconButton(
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        style = style,
        onClick = onClick,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
public fun IconButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape = IconButtonDefaults.ButtonSquareShape,
    colors: IconButtonColors = IconButtonDefaults.primaryColors(),
    elevation: ButtonElevation? = IconButtonDefaults.buttonElevation(),
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = IconButtonDefaults.ButtonPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    val style = remember(colors, shape, elevation) { IconButtonStyle(colors, shape, elevation) }
    IconButton(
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        style = style,
        onClick = onClick,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
private fun IconButtonComponent(
    modifier: Modifier,
    enabled: Boolean,
    loading: Boolean,
    style: IconButtonStyle,
    onClick: () -> Unit,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource,
    content: @Composable () -> Unit,
) {
    val containerColor = style.colors.containerColor(enabled).value
    val contentColor = style.colors.contentColor(enabled).value
    val borderStroke = remember(style.colors.borderColor) {
        BorderStroke(
            width = IconButtonDefaults.OutlineHeight,
            color = style.colors.borderColor,
        )
    }

    val shadowElevation = style.elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp

    BrutalContainer(
        shape = style.shape,
        elevation = shadowElevation,
        color = style.colors.borderColor,
    ) {
        val indication = remember(style, contentColor) {
            if (style.elevation != null) null else ripple(color = contentColor)
        }

        Surface(
            onClick = onClick,
            modifier =
                modifier
                    .defaultMinSize(
                        minWidth = IconButtonDefaults.ButtonSize,
                        minHeight = IconButtonDefaults.ButtonSize,
                    ).semantics { role = Role.Button },
            enabled = enabled,
            shape = style.shape,
            color = containerColor,
            contentColor = contentColor,
            border = borderStroke,
            interactionSource = interactionSource,
            indication = indication,
        ) {
            Box(
                modifier = Modifier.padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Crossfade(
                    targetState = loading,
                    modifier = Modifier.matchParentSize(),
                ) { target ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        if (target) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(contentPadding),
                                color = Color.Black,
                            )
                        } else {
                            content()
                        }
                    }
                }
            }
        }
    }
}

public enum class IconButtonVariant {
    Primary,
    PrimaryElevated,
    Secondary,
    SecondaryElevated,
    Tertiary,
    TertiaryElevated,
    Destructive,
    DestructiveElevated,
    Outlined,
    Ghost,
}

public object IconButtonDefaults {
    internal val ButtonSize = 44.dp
    internal val OutlineHeight = BrutalDefaults.BorderWidth
    public val ButtonPadding: PaddingValues = PaddingValues(4.dp)
    public val ButtonSquareShape: Shape @Composable get() = AppTheme.shapes.small
    public val ButtonCircleShape: Shape = RoundedCornerShape(percent = 50)

    @Composable
    public fun buttonElevation(
        defaultElevation: Dp = BrutalElevationDefaults.Small.default,
        pressedElevation: Dp = BrutalElevationDefaults.Small.pressed,
        focusedElevation: Dp = BrutalElevationDefaults.Small.focused,
        hoveredElevation: Dp = BrutalElevationDefaults.Small.hovered,
        disabledElevation: Dp = BrutalElevationDefaults.Small.disabled,
    ): ButtonElevation =
        ButtonElevation(
            defaultElevation = defaultElevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            disabledElevation = disabledElevation,
        )

    @Composable
    public fun styleFor(
        variant: IconButtonVariant,
        shape: Shape,
    ): IconButtonStyle =
        when (variant) {
            IconButtonVariant.Primary -> primary(shape)
            IconButtonVariant.PrimaryElevated -> primaryElevated(shape)
            IconButtonVariant.Secondary -> secondary(shape)
            IconButtonVariant.SecondaryElevated -> secondaryElevated(shape)
            IconButtonVariant.Tertiary -> tertiary(shape)
            IconButtonVariant.TertiaryElevated -> tertiaryElevated(shape)
            IconButtonVariant.Destructive -> destructive(shape)
            IconButtonVariant.DestructiveElevated -> destructiveElevated(shape)
            IconButtonVariant.Outlined -> outlined(shape)
            IconButtonVariant.Ghost -> ghost(shape)
        }

    @Composable
    public fun primaryColors(
        containerColor: Color = AppTheme.colors.primary,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.primary.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun primaryElevatedColors(
        containerColor: Color = AppTheme.colors.primary,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.primary.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun secondaryColors(
        containerColor: Color = AppTheme.colors.secondary,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.secondary.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun secondaryElevatedColors(
        containerColor: Color = AppTheme.colors.secondary,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.secondary.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun tertiaryColors(
        containerColor: Color = AppTheme.colors.tertiary,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.tertiary.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun tertiaryElevatedColors(
        containerColor: Color = AppTheme.colors.tertiary,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.tertiary.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun destructiveColors(
        containerColor: Color = AppTheme.colors.error,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.error.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun destructiveElevatedColors(
        containerColor: Color = AppTheme.colors.error,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.error.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun outlinedColors(
        containerColor: Color = LocalContainerColor.current,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = AppTheme.colors.onSurface,
        disabledContainerColor: Color = LocalContainerColor.current.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun ghostColors(
        containerColor: Color = AppTheme.colors.transparent,
        contentColor: Color = LocalContentColor.current,
        borderColor: Color = AppTheme.colors.transparent,
        disabledContainerColor: Color = AppTheme.colors.transparent,
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): IconButtonColors =
        IconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun primary(
        shape: Shape,
        colors: IconButtonColors = primaryColors(),
        elevation: ButtonElevation? = null,
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun primaryElevated(
        shape: Shape,
        colors: IconButtonColors = primaryElevatedColors(),
        elevation: ButtonElevation? = buttonElevation(),
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun secondary(
        shape: Shape,
        colors: IconButtonColors = secondaryColors(),
        elevation: ButtonElevation? = null,
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun secondaryElevated(
        shape: Shape,
        colors: IconButtonColors = secondaryElevatedColors(),
        elevation: ButtonElevation? = buttonElevation(),
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun tertiary(
        shape: Shape,
        colors: IconButtonColors = tertiaryColors(),
        elevation: ButtonElevation? = null,
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun tertiaryElevated(
        shape: Shape,
        colors: IconButtonColors = tertiaryElevatedColors(),
        elevation: ButtonElevation? = buttonElevation(),
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun destructive(
        shape: Shape,
        colors: IconButtonColors = destructiveColors(),
        elevation: ButtonElevation? = null,
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun destructiveElevated(
        shape: Shape,
        colors: IconButtonColors = destructiveElevatedColors(),
        elevation: ButtonElevation? = buttonElevation(),
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun outlined(
        shape: Shape,
        colors: IconButtonColors = outlinedColors(),
        elevation: ButtonElevation? = null,
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)

    @Composable
    public fun ghost(
        shape: Shape,
        colors: IconButtonColors = ghostColors(),
        elevation: ButtonElevation? = null,
    ): IconButtonStyle = IconButtonStyle(colors, shape, elevation)
}

@Immutable
public data class IconButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
) {
    @Composable
    public fun containerColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(if (enabled) containerColor else disabledContainerColor)

    @Composable
    public fun contentColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
}

@Immutable
public data class IconButtonStyle(
    val colors: IconButtonColors,
    val shape: Shape,
    val elevation: ButtonElevation? = null,
)

@Composable
private fun IconButtonVariantPreview(
    title: String,
    filled: IconButtonVariant,
    elevated: IconButtonVariant,
) {
    @Composable
    fun Preview(variant: IconButtonVariant) {
        IconButton(variant = variant, onClick = {}) {
            DummyIconForIconButtonPreview()
        }
        IconButton(variant = variant, enabled = false) {
            DummyIconForIconButtonPreview()
        }
        IconButton(variant = variant, loading = true) {
            DummyIconForIconButtonPreview()
        }
        IconButton(variant = variant, shape = IconButtonDefaults.ButtonCircleShape) {
            DummyIconForIconButtonPreview()
        }
    }

    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = title, style = AppTheme.typography.h3)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Preview(filled)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Preview(elevated)
        }
    }
}

@Composable
@Preview
internal fun IconButtonPrimaryPreview() {
    @Composable
    fun Preview() {
        IconButtonVariantPreview(
            title = "Primary",
            filled = IconButtonVariant.Primary,
            elevated = IconButtonVariant.PrimaryElevated,
        )
    }

    Column {
        AppPreview(isDarkTheme = false) { Preview() }
        AppPreview(isDarkTheme = true) { Preview() }
    }
}

@Composable
@Preview
internal fun IconButtonSecondaryPreview() {
    @Composable
    fun Preview() {
        IconButtonVariantPreview(
            title = "Secondary",
            filled = IconButtonVariant.Secondary,
            elevated = IconButtonVariant.SecondaryElevated,
        )
    }

    Column {
        AppPreview(isDarkTheme = false) { Preview() }
        AppPreview(isDarkTheme = true) { Preview() }
    }
}

@Composable
@Preview
internal fun IconButtonTertiaryPreview() {
    @Composable
    fun Preview() {
        IconButtonVariantPreview(
            title = "Tertiary",
            filled = IconButtonVariant.Tertiary,
            elevated = IconButtonVariant.TertiaryElevated,
        )
    }

    Column {
        AppPreview(isDarkTheme = false) { Preview() }
        AppPreview(isDarkTheme = true) { Preview() }
    }
}

@Composable
@Preview
internal fun IconButtonDestructivePreview() {
    @Composable
    fun Preview() {
        IconButtonVariantPreview(
            title = "Destructive",
            filled = IconButtonVariant.Destructive,
            elevated = IconButtonVariant.DestructiveElevated,
        )
    }

    Column {
        AppPreview(isDarkTheme = false) { Preview() }
        AppPreview(isDarkTheme = true) { Preview() }
    }
}

@Composable
@Preview
internal fun GhostIconButtonPreview() {
    val colors = listOf(
        AppTheme.colors.background,
        AppTheme.colors.primary,
        AppTheme.colors.secondary,
        AppTheme.colors.tertiary,
        AppTheme.colors.error,
        AppTheme.colors.surface,
    )
    AppPreview {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Ghost Icon Buttons", style = AppTheme.typography.h3)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                colors.forEach { color ->
                    CompositionLocalProvider(
                        LocalContentColor provides contentColorFor(color = color),
                    ) {
                        IconButton(
                            elevation = null,
                            colors = IconButtonDefaults.ghostColors(
                                containerColor = color,
                            ),
                        ) {
                            DummyIconForIconButtonPreview()
                        }
                    }
                }
            }

            Text(text = "Outlined Icon Button", style = AppTheme.typography.h3)
            IconButton(variant = IconButtonVariant.Outlined) {
                DummyIconForIconButtonPreview()
            }
        }
    }
}

@Composable
private fun DummyIconForIconButtonPreview() {
    Canvas(modifier = Modifier.size(16.dp)) {
        val center = size / 2f
        val radius = size.minDimension * 0.4f
        val strokeWidth = 4f
        val cap = StrokeCap.Round

        drawLine(
            color = Color.Black,
            start = Offset(center.width - radius, center.height),
            end = Offset(center.width + radius, center.height),
            strokeWidth = strokeWidth,
            cap = cap,
        )

        drawLine(
            color = Color.Black,
            start = Offset(center.width, center.height - radius),
            end = Offset(center.width, center.height + radius),
            strokeWidth = strokeWidth,
            cap = cap,
        )

        val diagonalRadius = radius * 0.75f
        drawLine(
            color = Color.Black,
            start =
                Offset(
                    center.width - diagonalRadius,
                    center.height - diagonalRadius,
                ),
            end =
                Offset(
                    center.width + diagonalRadius,
                    center.height + diagonalRadius,
                ),
            strokeWidth = strokeWidth,
            cap = cap,
        )

        drawLine(
            color = Color.Black,
            start =
                Offset(
                    center.width - diagonalRadius,
                    center.height + diagonalRadius,
                ),
            end =
                Offset(
                    center.width + diagonalRadius,
                    center.height - diagonalRadius,
                ),
            strokeWidth = strokeWidth,
            cap = cap,
        )
    }
}
