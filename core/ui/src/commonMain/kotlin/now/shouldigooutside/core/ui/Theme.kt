package now.shouldigooutside.core.ui

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Shapes
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import now.shouldigooutside.core.platform.isAndroid
import now.shouldigooutside.core.ui.components.Surface
import now.shouldigooutside.core.ui.foundation.ripple
import now.shouldigooutside.core.ui.icons.LocalPlatformIcon
import now.shouldigooutside.core.ui.icons.PlatformIcon

public object AppTheme {
    public val colors: Colors
        @ReadOnlyComposable @Composable
        get() = LocalColors.current

    public val typography: Typography
        @ReadOnlyComposable @Composable
        get() = LocalTypography.current

    public val shapes: Shapes
        @ReadOnlyComposable @Composable
        get() = LocalShapes.current

    public val spacing: Spacing
        @ReadOnlyComposable @Composable
        get() = LocalSpacing.current
}

public val LocalThemeIsDark: ProvidableCompositionLocal<Boolean> = compositionLocalOf {
    error("Not initialized")
}

public val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope> =
    compositionLocalOf { error("SharedTransitionScope not provided") }

public val LocalWindowSizeClass: ProvidableCompositionLocal<WindowSizeClass> = compositionLocalOf {
    error("WindowSizeClass not provided")
}

public val LocalUse24HourTime: ProvidableCompositionLocal<Boolean> = compositionLocalOf {
    false
}

@Composable
public fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onThemeChanged: @Composable (isDark: Boolean) -> Unit = {},
    content: @Composable () -> Unit,
) {
    val rippleIndication = ripple()
    val selectionColors = rememberTextSelectionColors(LightColors)
    val typography = provideTypography()
    val colors = if (isDarkTheme) DarkColors else LightColors

    val platformIcon = remember {
        if (isAndroid) PlatformIcon.Android else PlatformIcon.iOS
    }
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this,
            LocalThemeIsDark provides isDarkTheme,
            LocalPlatformIcon provides platformIcon,
            LocalUse24HourTime provides false,
            LocalColors provides colors.animate(),
            LocalTypography provides typography,
            LocalShapes provides Shapes,
            LocalIndication provides rippleIndication,
            LocalTextSelectionColors provides selectionColors,
            LocalContentColor provides colors.contentColorFor(colors.background),
            LocalContainerColor provides colors.surface,
            LocalTextStyle provides typography.body1,
        ) {
            onThemeChanged(!isDarkTheme)
            Surface {
                content()
            }
        }
    }
}

@Composable
public fun contentColorFor(color: Color): Color = AppTheme.colors.contentColorFor(color)

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
