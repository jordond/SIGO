package app.sigot.core.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import app.sigot.core.ui.components.foundation.ripple

public object AppTheme2 {
    public val colors: Colors
        @ReadOnlyComposable @Composable
        get() = LocalColors.current

    public val typography: Typography
        @ReadOnlyComposable @Composable
        get() = LocalTypography.current

    public val shapes: Shapes
        @ReadOnlyComposable @Composable
        get() = LocalShapes.current
}

@Composable
public fun AppTheme2(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val rippleIndication = ripple()
    val selectionColors = rememberTextSelectionColors(LightColors)
    val typography = provideTypography()
    val colors = if (isDarkTheme) DarkColors else LightColors

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides typography,
        LocalShapes provides Shapes,
        LocalIndication provides rippleIndication,
        LocalTextSelectionColors provides selectionColors,
        LocalContentColor provides colors.contentColorFor(colors.background),
        LocalTextStyle provides typography.body1,
        content = content,
    )
}

@Composable
public fun contentColorFor(color: Color): Color = AppTheme2.colors.contentColorFor(color)

@Composable
internal fun rememberTextSelectionColors(colorScheme: Colors): TextSelectionColors {
    val primaryColor = colorScheme.primary
    return remember(primaryColor) {
        TextSelectionColors(
            handleColor = primaryColor,
            backgroundColor = primaryColor.copy(alpha = 0.4f),
        )
    }
}
