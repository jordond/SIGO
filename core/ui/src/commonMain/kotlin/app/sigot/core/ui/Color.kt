package app.sigot.core.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White

internal val Black: Color = Color(0xFF000000)
internal val Gray900: Color = Color(0xFF282828)
internal val Gray800: Color = Color(0xFF4b4b4b)
internal val Gray700: Color = Color(0xFF5e5e5e)
internal val Gray600: Color = Color(0xFF727272)
internal val Gray500: Color = Color(0xFF868686)
internal val Gray400: Color = Color(0xFFC7C7C7)
internal val Gray300: Color = Color(0xFFDFDFDF)
internal val Gray200: Color = Color(0xFFE2E2E2)
internal val Gray100: Color = Color(0xFFF7F7F7)
internal val Gray50: Color = Color(0xFFFFFFFF)
internal val White: Color = Color(0xFFFFFFFF)

internal val Red900: Color = Color(0xFF520810)
internal val Red800: Color = Color(0xFF950f22)
internal val Red700: Color = Color(0xFFbb032a)
internal val Red600: Color = Color(0xFFde1135)
internal val Red500: Color = Color(0xFFf83446)
internal val Red400: Color = Color(0xFFfc7f79)
internal val Red300: Color = Color(0xFFffb2ab)
internal val Red200: Color = Color(0xFFffd2cd)
internal val Red100: Color = Color(0xFFffe1de)
internal val Red50: Color = Color(0xFFfff0ee)

internal val Blue900: Color = Color(0xFF276EF1)
internal val Blue800: Color = Color(0xFF3F7EF2)
internal val Blue700: Color = Color(0xFF578EF4)
internal val Blue600: Color = Color(0xFF6F9EF5)
internal val Blue500: Color = Color(0xFF87AEF7)
internal val Blue400: Color = Color(0xFF9FBFF8)
internal val Blue300: Color = Color(0xFFB7CEFA)
internal val Blue200: Color = Color(0xFFCFDEFB)
internal val Blue100: Color = Color(0xFFE7EEFD)
internal val Blue50: Color = Color(0xFFFFFFFF)

internal val Green950: Color = Color(0xFF0B4627)
internal val Green900: Color = Color(0xFF16643B)
internal val Green800: Color = Color(0xFF1A7544)
internal val Green700: Color = Color(0xFF178C4E)
internal val Green600: Color = Color(0xFF1DAF61)
internal val Green500: Color = Color(0xFF1FC16B)
internal val Green400: Color = Color(0xFF3EE089)
internal val Green300: Color = Color(0xFF84EBB4)
internal val Green200: Color = Color(0xFFC2F5DA)
internal val Green100: Color = Color(0xFFD0FBE9)
internal val Green50: Color = Color(0xFFE0FAEC)

@Immutable
public data class Colors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val error: Color,
    val onError: Color,
    val success: Color,
    val onSuccess: Color,
    val disabled: Color,
    val onDisabled: Color,
    val surface: Color,
    val onSurface: Color,
    val background: Color,
    val onBackground: Color,
    val outline: Color,
    val transparent: Color = Color.Transparent,
    val white: Color = White,
    val black: Color = Black,
    val text: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val scrim: Color,
    val elevation: Color,
)

internal val LightColors =
    Colors(
        primary = Black,
        onPrimary = White,
        secondary = Gray400,
        onSecondary = Black,
        tertiary = Blue900,
        onTertiary = White,
        surface = Gray200,
        onSurface = Black,
        error = Red600,
        onError = White,
        success = Green600,
        onSuccess = White,
        disabled = Gray100,
        onDisabled = Gray500,
        background = White,
        onBackground = Black,
        outline = Gray300,
        transparent = Color.Transparent,
        white = White,
        black = Black,
        text = Black,
        textSecondary = Gray700,
        textDisabled = Gray400,
        scrim = Color.Black.copy(alpha = 0.32f),
        elevation = Gray700,
    )

internal val DarkColors =
    Colors(
        primary = White,
        onPrimary = Black,
        secondary = Gray400,
        onSecondary = White,
        tertiary = Blue300,
        onTertiary = Black,
        surface = Gray900,
        onSurface = White,
        error = Red400,
        onError = Black,
        success = Green700,
        onSuccess = Black,
        disabled = Gray700,
        onDisabled = Gray500,
        background = Black,
        onBackground = White,
        outline = Gray800,
        transparent = Color.Transparent,
        white = White,
        black = Black,
        text = White,
        textSecondary = Gray300,
        textDisabled = Gray600,
        scrim = Color.Black.copy(alpha = 0.72f),
        elevation = Gray200,
    )

public val LocalColors: ProvidableCompositionLocal<Colors> = staticCompositionLocalOf { LightColors }
public val LocalContentColor: ProvidableCompositionLocal<Color> = compositionLocalOf { Color.Black }
public val LocalContentAlpha: ProvidableCompositionLocal<Float> = compositionLocalOf { 1f }

public fun Colors.contentColorFor(backgroundColor: Color): Color =
    when (backgroundColor) {
        primary -> onPrimary
        secondary -> onSecondary
        tertiary -> onTertiary
        surface -> onSurface
        error -> onError
        success -> onSuccess
        disabled -> onDisabled
        background -> onBackground
        else -> Color.Unspecified
    }

@Composable
internal fun Colors.animate(
    animationSpec: @Composable Transition.Segment<ColorScheme>.() -> FiniteAnimationSpec<Color> = {
        spring()
    },
    label: String = "ColorSchemeAnimation",
): Colors {
    val colorScheme = remember(this) {
        // Map the custom Colors object to a ColorScheme object, ignoring the ones that can't map
        ColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            secondary = secondary,
            onSecondary = onSecondary,
            tertiary = tertiary,
            onTertiary = onTertiary,
            error = error,
            onError = onError,
            primaryContainer = success,
            onPrimaryContainer = onSuccess,
            secondaryContainer = disabled,
            onSecondaryContainer = onDisabled,
            tertiaryContainer = text,
            onTertiaryContainer = textSecondary,
            onSurfaceVariant = textDisabled,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            outline = outline,
            scrim = scrim,
            inversePrimary = elevation,
            surfaceVariant = Color.Unspecified,
            surfaceTint = Color.Unspecified,
            inverseSurface = Color.Unspecified,
            inverseOnSurface = Color.Unspecified,
            errorContainer = Color.Unspecified,
            onErrorContainer = Color.Unspecified,
            outlineVariant = Color.Unspecified,
            surfaceBright = Color.Unspecified,
            surfaceDim = Color.Unspecified,
            surfaceContainer = Color.Unspecified,
            surfaceContainerHigh = Color.Unspecified,
            surfaceContainerHighest = Color.Unspecified,
            surfaceContainerLow = Color.Unspecified,
            surfaceContainerLowest = Color.Unspecified,
        )
    }

    val transition = updateTransition(targetState = colorScheme, label = "animated_colors")

    val primary by transition.animateColor(
        label = "color_primary",
        targetValueByState = { it.primary },
        transitionSpec = animationSpec,
    )
    val onPrimary by transition.animateColor(
        label = "color_onPrimary",
        targetValueByState = { it.onPrimary },
        transitionSpec = animationSpec,
    )
    val secondary by transition.animateColor(
        label = "color_secondary",
        targetValueByState = { it.secondary },
        transitionSpec = animationSpec,
    )
    val onSecondary by transition.animateColor(
        label = "color_onSecondary",
        targetValueByState = { it.onSecondary },
        transitionSpec = animationSpec,
    )
    val tertiary by transition.animateColor(
        label = "color_tertiary",
        targetValueByState = { it.tertiary },
        transitionSpec = animationSpec,
    )
    val onTertiary by transition.animateColor(
        label = "color_onTertiary",
        targetValueByState = { it.onTertiary },
        transitionSpec = animationSpec,
    )
    val error by transition.animateColor(
        label = "color_error",
        targetValueByState = { it.error },
        transitionSpec = animationSpec,
    )
    val onError by transition.animateColor(
        label = "color_onError",
        targetValueByState = { it.onError },
        transitionSpec = animationSpec,
    )
    val success by transition.animateColor(
        label = "color_success",
        targetValueByState = { it.primaryContainer },
        transitionSpec = animationSpec,
    )
    val onSuccess by transition.animateColor(
        label = "color_onSuccess",
        targetValueByState = { it.onPrimaryContainer },
        transitionSpec = animationSpec,
    )
    val disabled by transition.animateColor(
        label = "color_disabled",
        targetValueByState = { it.secondaryContainer },
        transitionSpec = animationSpec,
    )
    val onDisabled by transition.animateColor(
        label = "color_onDisabled",
        targetValueByState = { it.onSecondaryContainer },
        transitionSpec = animationSpec,
    )
    val surface by transition.animateColor(
        label = "color_surface",
        targetValueByState = { it.surface },
        transitionSpec = animationSpec,
    )
    val onSurface by transition.animateColor(
        label = "color_onSurface",
        targetValueByState = { it.onSurface },
        transitionSpec = animationSpec,
    )
    val background by transition.animateColor(
        label = "color_background",
        targetValueByState = { it.background },
        transitionSpec = animationSpec,
    )
    val onBackground by transition.animateColor(
        label = "color_onBackground",
        targetValueByState = { it.onBackground },
        transitionSpec = animationSpec,
    )
    val outline by transition.animateColor(
        label = "color_outline",
        targetValueByState = { it.outline },
        transitionSpec = animationSpec,
    )
    val text by transition.animateColor(
        label = "color_text",
        targetValueByState = { it.tertiaryContainer },
        transitionSpec = animationSpec,
    )
    val textSecondary by transition.animateColor(
        label = "color_textSecondary",
        targetValueByState = { it.onTertiaryContainer },
        transitionSpec = animationSpec,
    )
    val textDisabled by transition.animateColor(
        label = "color_textDisabled",
        targetValueByState = { it.onSurfaceVariant },
        transitionSpec = animationSpec,
    )
    val scrim by transition.animateColor(
        label = "color_scrim",
        targetValueByState = { it.scrim },
        transitionSpec = animationSpec,
    )
    val elevation by transition.animateColor(
        label = "color_elevation",
        targetValueByState = { it.inversePrimary },
        transitionSpec = animationSpec,
    )
    return copy(
        primary = primary,
        onPrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        error = error,
        onError = onError,
        success = success,
        onSuccess = onSuccess,
        disabled = disabled,
        onDisabled = onDisabled,
        surface = surface,
        onSurface = onSurface,
        background = background,
        onBackground = onBackground,
        outline = outline,
        text = text,
        textSecondary = textSecondary,
        textDisabled = textDisabled,
        scrim = scrim,
        elevation = elevation,
    )
}
