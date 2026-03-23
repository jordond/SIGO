package now.shouldigooutside.widget

import androidx.compose.ui.graphics.Color

internal object WidgetTheme {
    // Light theme colors (from core/ui Color.kt)
    val scoreYesLight = Color(0xFF90EE90) // Mint
    val scoreMaybeLight = Color(0xFFFFDB02) // YellowAlt
    val scoreNoLight = Color(0xFFFF6B6B) // Coral
    val backgroundLight = Color(0xFFFEECDE) // WarmBeige
    val surfaceLight = Color(0xFFFFF5EA) // LightBeige
    val textLight = Color(0xFF000000)
    val textSecondaryLight = Color(0xFF333333) // DarkGray

    // Dark theme colors
    val scoreYesDark = Color(0xFF7FFFD4) // BrightMint
    val scoreMaybeDark = Color(0xFFFCCF03) // BrightYellow
    val scoreNoDark = Color(0xFFFF8C91) // BrightCoral
    val backgroundDark = Color(0xFF141228) // NeoDarkBackground
    val surfaceDark = Color(0xFF2A2236) // NeoDarkSurface
    val textDark = Color(0xFFF8F8F8) // LightGray
    val textSecondaryDark = Color(0xFFAAAAAA) // MediumGray

    fun scoreColor(
        result: String,
        isDark: Boolean,
    ): Color =
        when (result) {
            "Yes" -> if (isDark) scoreYesDark else scoreYesLight
            "No" -> if (isDark) scoreNoDark else scoreNoLight
            else -> if (isDark) scoreMaybeDark else scoreMaybeLight
        }

    fun scoreTextColor(result: String): Color = Color.Black

    fun backgroundColor(isDark: Boolean): Color = if (isDark) backgroundDark else backgroundLight

    fun surfaceColor(isDark: Boolean): Color = if (isDark) surfaceDark else surfaceLight

    fun textColor(isDark: Boolean): Color = if (isDark) textDark else textLight

    fun textSecondaryColor(isDark: Boolean): Color = if (isDark) textSecondaryDark else textSecondaryLight
}
