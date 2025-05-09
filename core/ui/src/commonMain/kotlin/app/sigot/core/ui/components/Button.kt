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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContentColor
import app.sigot.core.ui.components.progressindicators.CircularProgressIndicator
import app.sigot.core.ui.foundation.ButtonElevation
import app.sigot.core.ui.ktx.disabled
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun Button(
    modifier: Modifier = Modifier,
    text: String? = null,
    textStyle: TextStyle = AppTheme.typography.button,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingContent: (@Composable () -> Unit)? = @Composable {
        val color = LocalContentColor.current
        CircularProgressIndicator(
            color = color,
            modifier = Modifier.size(20.dp),
            strokeWidth = 2.dp,
        )
    },
    variant: ButtonVariant = ButtonVariant.Primary,
    onClick: () -> Unit = {},
    contentPadding: PaddingValues = ButtonDefaults.contentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: (@Composable () -> Unit)? = null,
) {
    ButtonComponent(
        text = text,
        modifier = modifier,
        enabled = enabled,
        loading = loading,
        loadingContent = loadingContent,
        style = buttonStyleFor(variant),
        onClick = onClick,
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
        offset = shadowElevation,
        color = style.colors.borderColor,
        modifier = modifier,
    ) {
        Surface(
            onClick = onClick,
            modifier =
                Modifier
                    .defaultMinSize(minHeight = ButtonDefaults.MinHeight)
                    .semantics { role = Role.Button },
            enabled = enabled,
            shape = style.shape,
            color = containerColor,
            contentColor = contentColor,
            border = borderStroke,
            interactionSource = interactionSource,
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
    Outlined,
    Ghost,
}

@Composable
internal fun buttonStyleFor(variant: ButtonVariant): ButtonStyle =
    when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.primaryFilled()
        ButtonVariant.PrimaryElevated -> ButtonDefaults.primaryElevated()
        ButtonVariant.Secondary -> ButtonDefaults.secondaryFilled()
        ButtonVariant.SecondaryElevated -> ButtonDefaults.secondaryElevated()
        ButtonVariant.Tertiary -> ButtonDefaults.tertiaryFilled()
        ButtonVariant.TertiaryElevated -> ButtonDefaults.tertiaryElevated()
        ButtonVariant.Destructive -> ButtonDefaults.destructiveFilled()
        ButtonVariant.DestructiveElevated -> ButtonDefaults.destructiveElevated()
        ButtonVariant.Outlined -> ButtonDefaults.outlined()
        ButtonVariant.Ghost -> ButtonDefaults.ghost()
    }

@Suppress("ConstPropertyName")
internal object ButtonDefaults {
    internal val MinHeight = 44.dp
    internal val OutlineHeight = BrutalDefaults.BorderWidth
    private val ButtonHorizontalPadding = 16.dp
    private val ButtonVerticalPadding = 8.dp
    private val ButtonShape = RoundedCornerShape(12)
    private const val DisabledAlpha = 0.8f

    val contentPadding =
        PaddingValues(
            start = ButtonHorizontalPadding,
            top = ButtonVerticalPadding,
            end = ButtonHorizontalPadding,
            bottom = ButtonVerticalPadding,
        )

    private val filledShape = ButtonShape
    private val elevatedShape = ButtonShape

    @Composable
    fun buttonElevation() =
        ButtonElevation(
            defaultElevation = BrutalElevationDefaults.defaultElevation,
            pressedElevation = BrutalElevationDefaults.pressedElevation,
            focusedElevation = BrutalElevationDefaults.focusedElevation,
            hoveredElevation = BrutalElevationDefaults.hoveredElevation,
            disabledElevation = BrutalElevationDefaults.disabledElevation,
        )

    @Composable
    fun primaryFilled() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.primary,
                    contentColor = AppTheme.colors.onPrimary,
                    borderColor = BrutalDefaults.Color,
                    disabledContainerColor = AppTheme.colors.primary.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onPrimary,
                ),
            shape = filledShape,
            elevation = null,
            contentPadding = contentPadding,
        )

    @Composable
    fun primaryElevated() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.primary,
                    contentColor = AppTheme.colors.onPrimary,
                    borderColor = BrutalDefaults.Color,
                    disabledContainerColor = AppTheme.colors.primary.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onPrimary,
                ),
            shape = elevatedShape,
            elevation = buttonElevation(),
            contentPadding = contentPadding,
        )

    @Composable
    fun secondaryFilled() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.secondary,
                    contentColor = AppTheme.colors.onSecondary,
                    borderColor = BrutalDefaults.Color,
                    disabledContainerColor = AppTheme.colors.secondary.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onSecondary,
                ),
            shape = filledShape,
            elevation = null,
            contentPadding = contentPadding,
        )

    @Composable
    fun secondaryElevated() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.secondary,
                    contentColor = AppTheme.colors.onSecondary,
                    borderColor = BrutalDefaults.Color,
                    disabledContainerColor = AppTheme.colors.secondary.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onSecondary,
                ),
            shape = elevatedShape,
            elevation = buttonElevation(),
            contentPadding = contentPadding,
        )

    @Composable
    fun tertiaryFilled() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.tertiary,
                    contentColor = AppTheme.colors.onTertiary,
                    borderColor = BrutalDefaults.Color,
                    disabledContainerColor = AppTheme.colors.tertiary.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onTertiary,
                ),
            shape = filledShape,
            elevation = null,
            contentPadding = contentPadding,
        )

    @Composable
    fun tertiaryElevated() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.tertiary,
                    contentColor = AppTheme.colors.onTertiary,
                    borderColor = BrutalDefaults.Color,
                    disabledContainerColor = AppTheme.colors.tertiary.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onTertiary,
                ),
            shape = elevatedShape,
            elevation = buttonElevation(),
            contentPadding = contentPadding,
        )

    @Composable
    fun destructiveFilled() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.error,
                    contentColor = AppTheme.colors.onError,
                    borderColor = BrutalDefaults.Color,
                    disabledContainerColor = AppTheme.colors.error.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onError,
                ),
            shape = filledShape,
            elevation = null,
            contentPadding = contentPadding,
        )

    @Composable
    fun destructiveElevated() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.error,
                    contentColor = AppTheme.colors.onError,
                    borderColor = BrutalDefaults.Color,
                    disabledContainerColor = AppTheme.colors.error.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onError,
                ),
            shape = elevatedShape,
            elevation = buttonElevation(),
            contentPadding = contentPadding,
        )

    @Composable
    fun outlined() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.surface,
                    contentColor = AppTheme.colors.onSurface,
                    borderColor = AppTheme.colors.onSurface,
                    disabledContainerColor = AppTheme.colors.surface.disabled(DisabledAlpha),
                    disabledContentColor = AppTheme.colors.onSurface,
                ),
            shape = filledShape,
            elevation = null,
            contentPadding = contentPadding,
        )

    @Composable
    fun ghost() =
        ButtonStyle(
            colors =
                ButtonColors(
                    containerColor = AppTheme.colors.transparent,
                    contentColor = LocalContentColor.current,
                    borderColor = AppTheme.colors.transparent,
                    disabledContainerColor = AppTheme.colors.transparent,
                    disabledContentColor = AppTheme.colors.onDisabled,
                ),
            shape = filledShape,
            elevation = null,
            contentPadding = contentPadding,
        )
}

@Immutable
internal data class ButtonColors(
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
internal data class ButtonStyle(
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
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(text = "${title}Elevated", variant = elevated, onClick = {})
            Button(text = "Disabled", variant = elevated, enabled = false)
        }
    }
}

@Composable
private fun Primary() {
    ButtonVariantPreview(
        title = "Primary",
        filled = ButtonVariant.Primary,
        elevated = ButtonVariant.PrimaryElevated,
    )
}

@Composable
@Preview
internal fun PrimaryPreview() {
    AppPreview(isDarkTheme = false) {
        Primary()
    }
}

@Composable
@Preview
internal fun PrimaryDarkPreview() {
    AppPreview(isDarkTheme = true) {
        Primary()
    }
}

@Composable
private fun Secondary() {
    ButtonVariantPreview(
        title = "Secondary",
        filled = ButtonVariant.Secondary,
        elevated = ButtonVariant.SecondaryElevated,
    )
}

@Composable
@Preview
internal fun SecondaryPreview() {
    AppPreview(isDarkTheme = false) {
        Secondary()
    }
}

@Composable
@Preview
internal fun SecondaryDarkPreview() {
    AppPreview(isDarkTheme = true) {
        Secondary()
    }
}

@Composable
private fun Tertiary() {
    ButtonVariantPreview(
        title = "Tertiary",
        filled = ButtonVariant.Tertiary,
        elevated = ButtonVariant.TertiaryElevated,
    )
}

@Composable
@Preview
internal fun TertiaryPreview() {
    AppPreview(isDarkTheme = false) {
        Tertiary()
    }
}

@Composable
@Preview
internal fun TertiaryDarkPreview() {
    AppPreview(isDarkTheme = true) {
        Tertiary()
    }
}

@Composable
private fun Destructive() {
    ButtonVariantPreview(
        title = "Destructive",
        filled = ButtonVariant.Destructive,
        elevated = ButtonVariant.DestructiveElevated,
    )
}

@Composable
@Preview
internal fun DestructivePreview() {
    AppPreview(isDarkTheme = false) {
        Destructive()
    }
}

@Composable
@Preview
internal fun DestructiveDarkPreview() {
    AppPreview(isDarkTheme = true) {
        Destructive()
    }
}

@Composable
@Preview
internal fun GhostPreview(modifier: Modifier = Modifier) {
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
internal fun GhostDarkPreview(modifier: Modifier = Modifier) {
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
