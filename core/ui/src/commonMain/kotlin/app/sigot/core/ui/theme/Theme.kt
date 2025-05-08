package app.sigot.core.ui.theme

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.platform.isAndroid
import app.sigot.core.ui.icons.LocalPlatformIcon
import app.sigot.core.ui.icons.PlatformIcon

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(AppRadius.ExtraSmall.value),
    small = RoundedCornerShape(AppRadius.Small.value),
    medium = RoundedCornerShape(AppRadius.Medium.value),
    large = RoundedCornerShape(AppRadius.Large.value),
    extraLarge = RoundedCornerShape(AppRadius.ExtraLarge.value),
)

@Composable
public fun primaryBorderStroke(width: Dp = 1.dp): BorderStroke = BorderStroke(width, colors.primary)

@Composable
public fun primaryContainerBorderStroke(width: Dp = 1.dp): BorderStroke =
    BorderStroke(width, colors.primaryContainer)

@Composable
public fun secondaryBorderStroke(width: Dp = 1.dp): BorderStroke = BorderStroke(width, colors.secondary)

@Composable
public fun secondaryContainerBorderStroke(width: Dp = 1.dp): BorderStroke =
    BorderStroke(width, colors.secondaryContainer)

@Composable
public fun tertiaryBorderStroke(width: Dp = 1.dp): BorderStroke = BorderStroke(width, colors.tertiary)

@Composable
public fun tertiaryContainerBorderStroke(width: Dp = 1.dp): BorderStroke =
    BorderStroke(width, colors.tertiaryContainer)

public val LocalThemeIsDark: ProvidableCompositionLocal<Boolean> = compositionLocalOf {
    error("Not initialized")
}

public val LocalContainerColor: ProvidableCompositionLocal<Color> = compositionLocalOf {
    Color.Unspecified
}

public val LocalSurfaceColor: ProvidableCompositionLocal<Color> = compositionLocalOf {
    Color.Unspecified
}

public val LocalWindowSizeClass: ProvidableCompositionLocal<WindowSizeClass> = compositionLocalOf {
    error("WindowSizeClass not provided")
}

public val LocalSharedTransitionScope: ProvidableCompositionLocal<SharedTransitionScope> =
    compositionLocalOf { error("SharedTransitionScope not provided") }

public val colors: ColorScheme
    @Composable get() = MaterialTheme.colorScheme

@Suppress("UnusedReceiverParameter")
public val ColorScheme.hint
    @Composable get() = LocalContentColor.current.copy(alpha = 0.7f)

@Composable
public fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = remember(useDarkTheme) {
        if (useDarkTheme) {
            staticDarkScheme
        } else {
            staticLightScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme.animated(),
        typography = AppTypography,
        shapes = AppShapes,
    ) {
        val platformIcon = remember {
            if (isAndroid) PlatformIcon.Android else PlatformIcon.iOS
        }
        SharedTransitionLayout {
            CompositionLocalProvider(
                LocalThemeIsDark provides useDarkTheme,
                LocalSharedTransitionScope provides this,
                LocalContainerColor provides MaterialTheme.colorScheme.primaryContainer,
                LocalSurfaceColor provides MaterialTheme.colorScheme.surface,
                LocalPlatformIcon provides platformIcon,
            ) {
                SystemAppearance(useDarkTheme)
                Surface(content = content)
            }
        }
    }
}

@Composable
public fun AppThemePreview(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    AppTheme(useDarkTheme = useDarkTheme) {
        Surface {
            content()
        }
    }
}
