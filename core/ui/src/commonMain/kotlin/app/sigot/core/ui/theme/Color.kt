package app.sigot.core.ui.theme

import androidx.compose.ui.graphics.Color

public object BrandColors {
    public val Google: Color = Color(0xFFFFFFFF)
    public val Facebook: Color = Color(0xFF1877F2)
    public val Apple: Color = Color(0xFF000000)

    public object Content {
        public val Google: Color = Color(0xFF1F1F1F)
        public val Facebook: Color = Color(0xFFFFFFFF)
        public val Apple: Color = Color(0xFFFFFFFF)
    }
}

// Primary - Mystical violet tones for light theme
internal val PrimaryLight = Color(0xFF5B3FA0) // Deep mystical violet
internal val OnPrimaryLight = Color(0xFFFFFFFF) // White text for contrast
internal val PrimaryContainerLight = Color(0xFFD7BEFF) // More vibrant and distinct violet container
internal val OnPrimaryContainerLight = Color(0xFF1F1435) // Deep purple for text on container

// Secondary - Enigmatic plum purple tones
internal val SecondaryLight = Color(0xFF7248A0) // Rich plum purple
internal val OnSecondaryLight = Color(0xFFFFFFFF) // White text for contrast
internal val SecondaryContainerLight = Color(0xFFEAC7FF) // More vibrant lavender container
internal val OnSecondaryContainerLight = Color(0xFF381F4D) // Deep plum for text on container

// Tertiary - Accent mystical pink
internal val TertiaryLight = Color(0xFFAA2F8C) // Vibrant cosmic pink
internal val OnTertiaryLight = Color(0xFFFFFFFF) // White text for contrast
internal val TertiaryContainerLight = Color(0xFFFFD9F2) // Light pink container
internal val OnTertiaryContainerLight = Color(0xFF4A0D3A) // Deep magenta for text on container

// Error - Standard error colors
internal val ErrorLight = Color(0xFFBA1A1A) // Standard error red
internal val OnErrorLight = Color(0xFFFFFFFF) // White text on error
internal val ErrorContainerLight = Color(0xFFFFDAD6) // Light error container
internal val OnErrorContainerLight = Color(0xFF410002) // Deep red for text on container

// Background - Medium-light mystical theme
internal val BackgroundLight = Color(0xFFE6DCFA) // Light purple background with more saturation
internal val OnBackgroundLight = Color(0xFF1D1B20) // Near-black text on background

// Surface - Medium-light mystical variations
internal val SurfaceLight = Color(0xFFE6DCFA) // Same as background
internal val OnSurfaceLight = Color(0xFF1D1B20) // Near-black text on surface
internal val SurfaceVariantLight = Color(0xFFD6CAED) // Slightly deeper purple surface variant
internal val OnSurfaceVariantLight = Color(0xFF3A364A) // Darker purple-gray text for contrast

// Outline elements
internal val OutlineLight = Color(0xFF7A757F) // Medium dark outline
internal val OutlineVariantLight = Color(0xFFCBC4CE) // Light outline variant

// Miscellaneous
internal val ScrimLight = Color(0xFF000000) // Standard black scrim
internal val InverseSurfaceLight = Color(0xFF322F35) // Dark inverse surface
internal val InverseOnSurfaceLight = Color(0xFFF4EFF4) // Light text on inverse surface
internal val InversePrimaryLight = Color(0xFFCFBEFF) // Light primary for inverse

// Surface container variations
internal val SurfaceDimLight = Color(0xFFD8CEE8) // Dimmer purple than base surface
internal val SurfaceBrightLight = Color(0xFFEDE5FF) // Brighter but still purple
internal val SurfaceContainerLowestLight = Color(0xFFF2E8FF) // Lightest purple container
internal val SurfaceContainerLowLight = Color(0xFFEAE0F7) // Light purple for low container
internal val SurfaceContainerLight = Color(0xFFE1D6F0) // Medium-light purple for base container
internal val SurfaceContainerHighLight = Color(0xFFD9CCE7) // Slightly darker purple for high container
internal val SurfaceContainerHighestLight = Color(0xFFCEC0DE) // Even darker purple for highest container

// Primary - Deep violet with celestial undertones
internal val PrimaryDark = Color(0xFF9B7AE6) // Mystical violet
internal val OnPrimaryDark = Color(0xFF1F1435) // Deep midnight for text on primary
internal val PrimaryContainerDark = Color(0xFF402C6E) // Rich amethyst container
internal val OnPrimaryContainerDark = Color(0xFFE9DEFF) // Soft starlight for text on container

// Secondary - Enigmatic plum purple tones
internal val SecondaryDark = Color(0xFFD7ACFF) // Ethereal lavender
internal val OnSecondaryDark = Color(0xFF381F4D) // Deep plum for text on secondary
internal val SecondaryContainerDark = Color(0xFF563366) // Mystical tarot purple container
internal val OnSecondaryContainerDark = Color(0xFFF1DAFF) // Moonlit glow for text on container

// Tertiary - Accent mystical teal
internal val TertiaryDark = Color(0xFFFFA6E9) // Light cosmic pink
internal val OnTertiaryDark = Color(0xFF4A0D3A) // Deep magenta for text on tertiary
internal val TertiaryContainerDark = Color(0xFF702952) // Mid-tone pink container
internal val OnTertiaryContainerDark = Color(0xFFFFD9F2) // Very light pink for text on container

// Error - Standard error colors
internal val ErrorDark = Color(0xFFFFB4AB) // Light error red
internal val OnErrorDark = Color(0xFF690005) // Deep red for text on error
internal val ErrorContainerDark = Color(0xFF93000A) // Mid-tone error container
internal val OnErrorContainerDark = Color(0xFFFFDAD6) // Very light red for text on container

// Background - Deep purple as requested
internal val BackgroundDark = Color(0xFF0F0D2A) // Deep purple background
internal val OnBackgroundDark = Color(0xFFE6E1E5) // Light text on background

// Surface - Deep purple variations
internal val SurfaceDark = Color(0xFF0F0D2A) // Same deep purple as background
internal val OnSurfaceDark = Color(0xFFE6E1E5) // Light text on surface
internal val SurfaceVariantDark = Color(0xFF191643) // Slightly lighter purple surface variant
internal val OnSurfaceVariantDark = Color(0xFFCBC4CE) // Medium light text on surface variant

// Outline elements
internal val OutlineDark = Color(0xFF948F99) // Medium light outline
internal val OutlineVariantDark = Color(0xFF4A4458) // Darker outline variant

// Miscellaneous
internal val ScrimDark = Color(0xFF000000) // Standard black scrim
internal val InverseSurfaceDark = Color(0xFFE6E1E5) // Light inverse surface
internal val InverseOnSurfaceDark = Color(0xFF313033) // Dark text on inverse surface
internal val InversePrimaryDark = Color(0xFF6B50BB) // Medium-dark inverse primary

// Surface container variations
internal val SurfaceDimDark = Color(0xFF120F30) // Slightly darker than base surface
internal val SurfaceBrightDark = Color(0xFF26204F) // Brighter purple than base surface
internal val SurfaceContainerLowestDark = Color(0xFF0C0A22) // Lowest container level
internal val SurfaceContainerLowDark = Color(0xFF161339) // Low container level
internal val SurfaceContainerDark = Color(0xFF191643) // Base container level
internal val SurfaceContainerHighDark = Color(0xFF1E1A4D) // High container level
internal val SurfaceContainerHighestDark = Color(0xFF252057) // Highest container level

public val Seed: Color = Color(0xFF4B0094)
