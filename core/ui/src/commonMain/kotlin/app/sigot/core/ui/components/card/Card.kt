package app.sigot.core.ui.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.LocalTypography
import app.sigot.core.ui.components.BrutalContainer
import app.sigot.core.ui.components.BrutalDefaults
import app.sigot.core.ui.components.BrutalElevationDefaults
import app.sigot.core.ui.components.Surface
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun Card(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.Shape,
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke = CardDefaults.cardBorder(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = colors.containerColor(enabled = true).value,
        contentColor = colors.contentColor(enabled = true).value,
        border = border,
    ) {
        CompositionLocalProvider(
            LocalContainerColor provides colors.containerColor(enabled = true).value,
        ) {
            Column(content = content)
        }
    }
}

@Composable
public fun Card(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.Shape,
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke = CardDefaults.cardBorder(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        color = colors.containerColor(enabled).value,
        contentColor = colors.contentColor(enabled).value,
        border = border,
        interactionSource = interactionSource,
    ) {
        CompositionLocalProvider(
            LocalContainerColor provides colors.containerColor(enabled).value,
        ) {
            Column(content = content)
        }
    }
}

@Composable
public fun ElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.ElevatedShape,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke = CardDefaults.cardBorder(),
    content: @Composable ColumnScope.() -> Unit,
) {
    val elevationValue by elevation.shadowElevation(enabled = true, interactionSource = null)
    BrutalContainer(
        shape = shape,
        elevation = elevationValue,
        color = CardDefaults.BorderColor,
        extraY = true,
    ) {
        Card(
            modifier = modifier,
            shape = shape,
            border = border,
            colors = colors,
            content = content,
        )
    }
}

@Composable
public fun ElevatedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CardDefaults.Shape,
    colors: CardColors = CardDefaults.elevatedCardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke = CardDefaults.cardBorder(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable ColumnScope.() -> Unit,
) {
    val elevationValue by elevation.shadowElevation(enabled, interactionSource)
    BrutalContainer(
        shape = shape,
        elevation = elevationValue,
        color = CardDefaults.BorderColor,
    ) {
        Card(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            border = border,
            colors = colors,
            interactionSource = interactionSource,
            content = content,
        )
    }
}

public object CardDefaults {
    public val Shape: CornerBasedShape @Composable get() = AppTheme.shapes.large
    public val ElevatedShape: CornerBasedShape @Composable get() = Shape
    public val BorderColor: Color @Composable get() = BrutalDefaults.Color
    private val BorderWidth = BrutalDefaults.BorderWidth

    @Composable
    public fun cardElevation(
        defaultElevation: Dp = BrutalElevationDefaults.Medium.default,
        pressedElevation: Dp = BrutalElevationDefaults.Medium.pressed,
        focusedElevation: Dp = BrutalElevationDefaults.Medium.focused,
        hoveredElevation: Dp = BrutalElevationDefaults.Medium.hovered,
        draggedElevation: Dp = BrutalElevationDefaults.Medium.dragged,
        disabledElevation: Dp = BrutalElevationDefaults.Medium.disabled,
    ): CardElevation =
        CardElevation(
            defaultElevation = defaultElevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            draggedElevation = draggedElevation,
            disabledElevation = disabledElevation,
        )

    @Composable
    public fun cardColors(
        containerColor: Color = AppTheme.colors.surface,
        contentColor: Color = AppTheme.colors.onSurface,
        disabledContainerColor: Color = AppTheme.colors.disabled,
        disabledContentColor: Color = AppTheme.colors.onDisabled,
    ): CardColors =
        CardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun elevatedCardColors(
        containerColor: Color = AppTheme.colors.surface,
        contentColor: Color = AppTheme.colors.onSurface,
        disabledContainerColor: Color = AppTheme.colors.disabled,
        disabledContentColor: Color = AppTheme.colors.onDisabled,
    ): CardColors =
        CardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        )

    @Composable
    public fun cardBorder(color: Color = BorderColor): BorderStroke =
        remember(color) { BorderStroke(BorderWidth, color) }
}

@ConsistentCopyVisibility
@Immutable
public data class CardColors internal constructor(
    private val containerColor: Color,
    private val contentColor: Color,
    private val disabledContainerColor: Color,
    private val disabledContentColor: Color,
) {
    @Composable
    internal fun containerColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(if (enabled) containerColor else disabledContainerColor)

    @Composable
    internal fun contentColor(enabled: Boolean): State<Color> =
        rememberUpdatedState(if (enabled) contentColor else disabledContentColor)
}

@Composable
@Preview
internal fun CardComponentLight() {
    AppPreview(isDarkTheme = false) {
        CardComponentSample()
    }
}

@Composable
@Preview
internal fun CardComponentDark() {
    AppPreview(isDarkTheme = true) {
        CardComponentSample()
    }
}

@Composable
internal fun CardComponentSample() {
    val cardModifier = Modifier
        .fillMaxWidth()
        .height(120.dp)

    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Column {
                Text(text = "Default Card", style = LocalTypography.current.h3)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = cardModifier,
                ) {}
            }

            Column {
                Text(text = "Disabled Card", style = LocalTypography.current.h3)
                Card(
                    modifier = cardModifier,
                    onClick = { },
                    enabled = false,
                ) {}
            }

            Column {
                Text(text = "Elevated Card", style = LocalTypography.current.h3)
                Spacer(modifier = Modifier.height(8.dp))

                ElevatedCard(
                    modifier = cardModifier,
                    onClick = { },
                ) {}
            }

            Column {
                Text(text = "Disabled Elevated Card", style = LocalTypography.current.h3)
                ElevatedCard(
                    modifier = cardModifier,
                    onClick = { },
                    enabled = false,
                ) {}
            }
        }
    }
}
