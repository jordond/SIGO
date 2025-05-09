package app.sigot.core.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val Gray900: Color = Color(0xFF282828)
internal val Gray800: Color = Color(0xFF4b4b4b)
internal val Gray700: Color = Color(0xFF5e5e5e)
internal val Gray600: Color = Color(0xFF727272)
internal val Gray500: Color = Color(0xFF868686)
internal val Gray400: Color = Color(0xFFC7C7C7)
internal val Gray300: Color = Color(0xFFDFDFDF)
internal val Gray200: Color = Color(0xFFE2E2E2)

internal val Red400: Color = Color(0xFFfc7f79)
internal val Blue300: Color = Color(0xFFB7CEFA)
internal val Green700: Color = Color(0xFF178C4E)

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
    val white: Color = Color.White,
    val black: Color = Color.Black,
    val text: Color,
    val textSecondary: Color,
    val textDisabled: Color,
    val scrim: Color,
    val elevation: Color,
)

private val Yellow = Color(0xFFFFD738) // Bright yellow - from palette
private val YellowAlt = Color(0xFFffdb02)
private val Coral = Color(0xFFFF6B6B) // Coral red - from palette
private val Mint = Color(0xFF90EE90) // Light mint green - for success
private val LightBlue = Color(0xFF87CEEB) // Light blue - from palette
private val Pink = Color(0xFFFF69B4) // Pink - from palette
private val LightPurple = Color(0xFFA388EE) // Light purple - from palette
private val WarmBeige = Color(0xFFFEECDE) // Warm beige for backgrounds
private val LightBeige = Color(0xFFFFF5EA)
private val LightGray = Color(0xFFF8F8F8) // Light gray for backgrounds
private val MediumGray = Color(0xFFAAAAAA) // Medium gray for disabled states
private val DarkGray = Color(0xFF333333) // Dark gray for secondary text
private val ScrimColor = Color(0x99000000) // Semi-transparent black for scrims

// Dark theme colors
private val DarkNavy = Color(0xFF1A1A2E) // Dark navy for backgrounds
private val DeepBlue = Color(0xFF16213E) // Deeper blue for surfaces
private val DarkPurple = Color(0xFF35155D) // Dark purple
private val BrightYellow = Color(0xFFFCCF03) // More saturated yellow
private val BrightCoral = Color(0xFFFF8C91) // Brighter coral
private val BrightMint = Color(0xFF7FFFD4) // Brighter mint
private val BrightBlue = Color(0xFF5599FF) // Brighter blue
private val VividPink = Color(0xFFFF79C6) // More vivid pink
private val NeoDarkBackground = Color(0xFF141228)
private val NeoDarkSurface = Color(0xFF2A2236)

private val DarkScrim = Color(0xCC000000) // Darker scrim for dark theme

internal val LightColors = Colors(
    primary = YellowAlt,
    onPrimary = Color.Black,
    secondary = LightBlue,
    onSecondary = Color.Black,
    tertiary = Pink,
    onTertiary = Color.Black,
    error = Coral,
    onError = Color.Black,
    success = Mint,
    onSuccess = Color.Black,
    disabled = MediumGray,
    onDisabled = Color.White,
    surface = LightBeige,
    onSurface = Color.Black,
    background = WarmBeige,
    onBackground = Color.Black,
    outline = Color.Black,
    transparent = Color.Transparent,
    text = Color.Black,
    textSecondary = DarkGray,
    textDisabled = MediumGray,
    scrim = ScrimColor,
    elevation = Color.Black,
)

public val DarkColors: Colors = Colors(
    primary = BrightYellow,
    onPrimary = DarkNavy,
    secondary = BrightBlue,
    onSecondary = Color.Black,
    tertiary = VividPink,
    onTertiary = Color.Black,
    error = BrightCoral,
    onError = Color.Black,
    success = BrightMint,
    onSuccess = Color.Black,
    disabled = DarkGray,
    onDisabled = MediumGray,
    surface = NeoDarkSurface,
    onSurface = Color.White,
    background = NeoDarkBackground,
    onBackground = Color.White,
    outline = Color.Black,
    transparent = Color.Transparent,
    text = LightGray,
    textSecondary = MediumGray,
    textDisabled = DarkGray,
    scrim = DarkScrim,
    elevation = Color.Black,
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
