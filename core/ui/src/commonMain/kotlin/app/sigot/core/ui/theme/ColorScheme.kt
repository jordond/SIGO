package app.sigot.core.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

internal val staticLightScheme: ColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    scrim = ScrimLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
)

internal val staticDarkScheme: ColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,
    scrim = ScrimDark,
    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
)

@Composable
internal fun ColorScheme.animated(
    animationSpec: AnimationSpec<Color> = spring(stiffness = Spring.StiffnessLow),
): ColorScheme =
    copy(
        primary = primary.animate(animationSpec),
        primaryContainer = primaryContainer.animate(animationSpec),
        secondary = secondary.animate(animationSpec),
        secondaryContainer = secondaryContainer.animate(animationSpec),
        tertiary = tertiary.animate(animationSpec),
        tertiaryContainer = tertiaryContainer.animate(animationSpec),
        background = background.animate(animationSpec),
        surface = surface.animate(animationSpec),
        surfaceTint = surfaceTint.animate(animationSpec),
        surfaceBright = surfaceBright.animate(animationSpec),
        surfaceDim = surfaceDim.animate(animationSpec),
        surfaceContainer = surfaceContainer.animate(animationSpec),
        surfaceContainerHigh = surfaceContainerHigh.animate(animationSpec),
        surfaceContainerHighest = surfaceContainerHighest.animate(animationSpec),
        surfaceContainerLow = surfaceContainerLow.animate(animationSpec),
        surfaceContainerLowest = surfaceContainerLowest.animate(animationSpec),
        surfaceVariant = surfaceVariant.animate(animationSpec),
        error = error.animate(animationSpec),
        errorContainer = errorContainer.animate(animationSpec),
        onPrimary = onPrimary.animate(animationSpec),
        onPrimaryContainer = onPrimaryContainer.animate(animationSpec),
        onSecondary = onSecondary.animate(animationSpec),
        onSecondaryContainer = onSecondaryContainer.animate(animationSpec),
        onTertiary = onTertiary.animate(animationSpec),
        onTertiaryContainer = onTertiaryContainer.animate(animationSpec),
        onBackground = onBackground.animate(animationSpec),
        onSurface = onSurface.animate(animationSpec),
        onSurfaceVariant = onSurfaceVariant.animate(animationSpec),
        onError = onError.animate(animationSpec),
        onErrorContainer = onErrorContainer.animate(animationSpec),
        inversePrimary = inversePrimary.animate(animationSpec),
        inverseSurface = inverseSurface.animate(animationSpec),
        inverseOnSurface = inverseOnSurface.animate(animationSpec),
        outline = outline.animate(animationSpec),
        outlineVariant = outlineVariant.animate(animationSpec),
        scrim = scrim.animate(animationSpec),
    )

@Composable
private fun Color.animate(animationSpec: AnimationSpec<Color>): Color =
    animateColorAsState(this, animationSpec).value
