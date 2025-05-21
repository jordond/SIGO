package app.sigot.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
public fun Button(
    style: ButtonStyle,
    modifier: Modifier = Modifier,
    text: String? = null,
    textStyle: TextStyle = AppTheme.typography.button,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingContent: (@Composable () -> Unit)? = ButtonDefaults.LoadingIndicator(),
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = ButtonDefaults.contentPadding,
    minHeight: Dp = ButtonDefaults.MinHeight,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: (@Composable () -> Unit)? = null,
) {
    ButtonComponent(
        modifier = modifier,
        text = text,
        textStyle = textStyle,
        enabled = enabled,
        loading = loading,
        loadingContent = loadingContent,
        style = style,
        onClick = onClick,
        minHeight = minHeight,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
public fun Button(
    modifier: Modifier = Modifier,
    text: String? = null,
    textStyle: TextStyle = AppTheme.typography.button,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingContent: (@Composable () -> Unit)? = ButtonDefaults.LoadingIndicator(),
    variant: ButtonVariant = ButtonVariant.Primary,
    shape: Shape? = null,
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = ButtonDefaults.contentPadding,
    minHeight: Dp = ButtonDefaults.MinHeight,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: (@Composable () -> Unit)? = null,
) {
    val style = buttonStyleFor(variant)
    Button(
        style = if (shape != null) style.copy(shape = shape) else style,
        text = text,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        loadingContent = loadingContent,
        onClick = onClick,
        contentPadding = contentPadding,
        minHeight = minHeight,
        textStyle = textStyle,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
public fun Button(
    modifier: Modifier = Modifier,
    text: String? = null,
    textStyle: TextStyle = AppTheme.typography.button,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingContent: (@Composable () -> Unit)? = ButtonDefaults.LoadingIndicator(),
    shape: Shape = ButtonDefaults.ButtonShape,
    colors: ButtonColors = ButtonDefaults.primaryColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    minHeight: Dp = ButtonDefaults.MinHeight,
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = ButtonDefaults.contentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: (@Composable () -> Unit)? = null,
) {
    val style = remember(colors, shape, elevation, contentPadding) {
        ButtonStyle(colors, shape, elevation, contentPadding)
    }

    Button(
        style = style,
        text = text,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        loadingContent = loadingContent,
        onClick = onClick,
        minHeight = minHeight,
        textStyle = textStyle,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content,
    )
}

@Composable
internal fun ButtonComponent(
    text: String? = null,
    modifier: Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingContent: (@Composable () -> Unit)? = null,
    style: ButtonStyle,
    minHeight: Dp = ButtonDefaults.MinHeight,
    textStyle: TextStyle = AppTheme.typography.button,
    onClick: () -> Unit,
    contentPadding: PaddingValues = ButtonDefaults.contentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: (@Composable () -> Unit)? = null,
) {
    val containerColor = style.colors.containerColor(enabled).value
    val contentColor = style.colors.contentColor(enabled).value
    val borderStroke = remember(style.colors.borderColor) {
        BorderStroke(
            width = ButtonDefaults.OutlineHeight,
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
                    .defaultMinSize(minHeight = minHeight)
                    .semantics { role = Role.Button },
            enabled = enabled,
            shape = style.shape,
            color = containerColor,
            contentColor = contentColor,
            border = borderStroke,
            interactionSource = interactionSource,
            indication = indication,
        ) {
            CompositionLocalProvider(
                LocalContainerColor provides containerColor,
            ) {
                DefaultButtonContent(
                    text = text,
                    textStyle = textStyle,
                    loading = loading,
                    loadingContent = loadingContent,
                    contentColor = contentColor,
                    content = content,
                    modifier = Modifier.padding(contentPadding),
                )
            }
        }
    }
}

@Composable
private fun DefaultButtonContent(
    modifier: Modifier = Modifier,
    text: String? = null,
    textStyle: TextStyle = AppTheme.typography.button,
    loading: Boolean,
    contentColor: Color,
    loadingContent: (@Composable () -> Unit)?,
    content: (@Composable () -> Unit)? = null,
) {
    if (text?.isNotEmpty() == true) {
        Row(
            modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (loading) {
                loadingContent?.invoke()
            } else {
                Text(
                    text = AnnotatedString(text = text),
                    textAlign = TextAlign.Center,
                    style = textStyle,
                    overflow = TextOverflow.Clip,
                    color = contentColor,
                    autoSize = TextAutoSize.StepBased(maxFontSize = textStyle.fontSize),
                )
            }
        }
    } else if (content != null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

public enum class ButtonVariant {
    Primary,
    PrimaryElevated,
    Secondary,
    SecondaryElevated,
    Tertiary,
    TertiaryElevated,
    Destructive,
    DestructiveElevated,
    Elevated,
    Outlined,
    Ghost,
}

@Composable
internal fun buttonStyleFor(variant: ButtonVariant): ButtonStyle =
    when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.primary()
        ButtonVariant.PrimaryElevated -> ButtonDefaults.primaryElevated()
        ButtonVariant.Secondary -> ButtonDefaults.secondary()
        ButtonVariant.SecondaryElevated -> ButtonDefaults.secondaryElevated()
        ButtonVariant.Tertiary -> ButtonDefaults.tertiary()
        ButtonVariant.TertiaryElevated -> ButtonDefaults.tertiaryElevated()
        ButtonVariant.Destructive -> ButtonDefaults.destructive()
        ButtonVariant.DestructiveElevated -> ButtonDefaults.destructiveElevated()
        ButtonVariant.Elevated -> ButtonDefaults.elevated()
        ButtonVariant.Outlined -> ButtonDefaults.outlined()
        ButtonVariant.Ghost -> ButtonDefaults.ghost()
    }

@Suppress("ConstPropertyName")
public object ButtonDefaults {
    internal val MinHeight = 44.dp
    internal val MinWidth = 58.dp
    internal val OutlineHeight = BrutalDefaults.BorderWidth
    private val ButtonHorizontalPadding = 16.dp
    private val ButtonVerticalPadding = 8.dp
    public val ButtonShape: Shape @Composable get() = AppTheme.shapes.medium

    internal val contentPadding =
        PaddingValues(
            start = ButtonHorizontalPadding,
            top = ButtonVerticalPadding,
            end = ButtonHorizontalPadding,
            bottom = ButtonVerticalPadding,
        )

    private val filledShape @Composable get() = ButtonShape
    private val elevatedShape @Composable get() = ButtonShape

    private val TextButtonHorizontalPadding = 12.dp

    @Composable
    public fun LoadingIndicator(): @Composable () -> Unit =
        {
            val color = LocalContentColor.current
            CircularProgressIndicator(
                color = color,
                modifier = Modifier.size(20.dp),
            )
        }

    @Composable
    public fun buttonElevation(
        defaultElevation: Dp = BrutalElevationDefaults.Medium.default,
        pressedElevation: Dp = BrutalElevationDefaults.Medium.pressed,
        focusedElevation: Dp = BrutalElevationDefaults.Medium.focused,
        hoveredElevation: Dp = BrutalElevationDefaults.Medium.hovered,
        disabledElevation: Dp = BrutalElevationDefaults.Medium.disabled,
    ): ButtonElevation =
        ButtonElevation(
            defaultElevation = defaultElevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            disabledElevation = disabledElevation,
        )

    @Composable
    public fun primaryColors(
        containerColor: Color = AppTheme.colors.primary,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = AppTheme.colors.primary.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): ButtonColors =
        ButtonColors(
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
    ): ButtonColors =
        ButtonColors(
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
    ): ButtonColors =
        ButtonColors(
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
    ): ButtonColors =
        ButtonColors(
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
    ): ButtonColors =
        ButtonColors(
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
    ): ButtonColors =
        ButtonColors(
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
    ): ButtonColors =
        ButtonColors(
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
    ): ButtonColors =
        ButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun elevatedColors(
        containerColor: Color = LocalContainerColor.current,
        contentColor: Color = contentColorFor(containerColor),
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = LocalContainerColor.current.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): ButtonColors =
        ButtonColors(
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
        borderColor: Color = BrutalDefaults.Color,
        disabledContainerColor: Color = LocalContainerColor.current.disabled(DisabledAlpha),
        disabledContentColor: Color = contentColorFor(disabledContainerColor),
    ): ButtonColors =
        ButtonColors(
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
        disabledContentColor: Color = AppTheme.colors.onDisabled,
    ): ButtonColors =
        ButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            borderColor = borderColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun primary(
        colors: ButtonColors = primaryColors(),
        shape: Shape = filledShape,
        elevation: ButtonElevation? = null,
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun primaryElevated(
        colors: ButtonColors = primaryElevatedColors(),
        shape: Shape = elevatedShape,
        elevation: ButtonElevation? = buttonElevation(),
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun secondary(
        colors: ButtonColors = secondaryColors(),
        shape: Shape = filledShape,
        elevation: ButtonElevation? = null,
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun secondaryElevated(
        colors: ButtonColors = secondaryElevatedColors(),
        shape: Shape = elevatedShape,
        elevation: ButtonElevation? = buttonElevation(),
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun tertiary(
        colors: ButtonColors = tertiaryColors(),
        shape: Shape = filledShape,
        elevation: ButtonElevation? = null,
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun tertiaryElevated(
        colors: ButtonColors = tertiaryElevatedColors(),
        shape: Shape = elevatedShape,
        elevation: ButtonElevation? = buttonElevation(),
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun destructive(
        colors: ButtonColors = destructiveColors(),
        shape: Shape = filledShape,
        elevation: ButtonElevation? = null,
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun destructiveElevated(
        colors: ButtonColors = destructiveElevatedColors(),
        shape: Shape = elevatedShape,
        elevation: ButtonElevation? = buttonElevation(),
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun elevated(
        colors: ButtonColors = elevatedColors(),
        shape: Shape = filledShape,
        elevation: ButtonElevation? = buttonElevation(),
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun outlined(
        colors: ButtonColors = outlinedColors(),
        shape: Shape = filledShape,
        elevation: ButtonElevation? = null,
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)

    @Composable
    public fun ghost(
        colors: ButtonColors = ghostColors(),
        shape: Shape = filledShape,
        elevation: ButtonElevation? = null,
        contentPadding: PaddingValues = this.contentPadding,
    ): ButtonStyle = ButtonStyle(colors, shape, elevation, contentPadding)
}

@Immutable
public data class ButtonColors(
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
) {
    @Composable
    internal fun containerColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(newValue = if (enabled) containerColor else disabledContainerColor)

    @Composable
    internal fun contentColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(newValue = if (enabled) contentColor else disabledContentColor)
}

@Immutable
public data class ButtonStyle(
    val colors: ButtonColors,
    val shape: Shape,
    val elevation: ButtonElevation? = null,
    val contentPadding: PaddingValues,
)

@Composable
internal fun ButtonVariantPreview(
    title: String,
    filled: ButtonVariant,
    elevated: ButtonVariant,
) {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = title,
            style = AppTheme.typography.h2,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(text = "${title}Filled", variant = filled, onClick = {})
            Button(text = "Disabled", variant = filled, enabled = false)
            Button(text = "Loading", variant = filled, loading = true)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(text = "${title}Elevated", variant = elevated, onClick = {})
            Button(text = "Disabled", variant = elevated, enabled = false)
            Button(text = "Loading", variant = filled, loading = true)
        }
    }
}

@Composable
@Preview
internal fun ButtonPrimaryPreview() {
    @Composable
    fun Preview() {
        ButtonVariantPreview(
            title = "Primary",
            filled = ButtonVariant.Primary,
            elevated = ButtonVariant.PrimaryElevated,
        )
    }
    Column {
        AppPreview(isDarkTheme = false) { Preview() }
        AppPreview(isDarkTheme = true) { Preview() }
    }
}

@Composable
@Preview
internal fun ButtonSecondaryPreview() {
    @Composable
    fun Preview() {
        ButtonVariantPreview(
            title = "Secondary",
            filled = ButtonVariant.Secondary,
            elevated = ButtonVariant.SecondaryElevated,
        )
    }
    Column {
        AppPreview(isDarkTheme = false) { Preview() }
        AppPreview(isDarkTheme = true) { Preview() }
    }
}

@Composable
@Preview
internal fun ButtonTertiaryPreview() {
    @Composable
    fun Preview() {
        ButtonVariantPreview(
            title = "Tertiary",
            filled = ButtonVariant.Tertiary,
            elevated = ButtonVariant.TertiaryElevated,
        )
    }
    Column {
        AppPreview(isDarkTheme = false) { Preview() }
        AppPreview(isDarkTheme = true) { Preview() }
    }
}

@Composable
@Preview
internal fun ButtonDestructivePreview() {
    @Composable
    fun Preview() {
        ButtonVariantPreview(
            title = "Destructive",
            filled = ButtonVariant.Destructive,
            elevated = ButtonVariant.DestructiveElevated,
        )
    }
    Column {
        AppPreview(isDarkTheme = false) { Preview() }
        AppPreview(isDarkTheme = true) { Preview() }
    }
}

@Composable
@Preview
internal fun ButtonGhostPreview(modifier: Modifier = Modifier) {
    AppPreview(isDarkTheme = false) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(AppTheme.colors.surface)
                .padding(16.dp),
        ) {
            Button(text = "Outlined", variant = ButtonVariant.Outlined, onClick = {})
            Button(text = "Ghost", variant = ButtonVariant.Ghost, onClick = {})
        }
    }
}

@Composable
@Preview
internal fun ButtonGhostDarkPreview(modifier: Modifier = Modifier) {
    AppPreview(isDarkTheme = true) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(AppTheme.colors.surface)
                .padding(16.dp),
        ) {
            Button(text = "Outlined", variant = ButtonVariant.Outlined, onClick = {})
            Button(text = "Ghost", variant = ButtonVariant.Ghost, onClick = {})
        }
    }
}
