package app.sigot.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import app.sigot.core.resources.LexendMega
import app.sigot.core.resources.PublicSans
import app.sigot.core.resources.PublicSans_Italic
import app.sigot.core.resources.Res
import org.jetbrains.compose.resources.Font

public val LexendMega: FontFamily
    @Composable
    get() = FontFamily(
        Font(
            Res.font.LexendMega,
            variationSettings = FontVariation.Settings(
                FontVariation.weight(700),
            ),
        ),
    )

public val PublicSans: FontFamily
    @Composable
    get() = FontFamily(
        listOf(
            Font(Res.font.PublicSans),
            Font(Res.font.PublicSans_Italic, style = FontStyle.Italic),
        ),
    )

public val AppTypography: Typography
    @Composable get() = createTypographyWith(PublicSans).run {
        copy(
            displayLarge = displayLarge.copy(fontFamily = LexendMega),
            displayMedium = displayMedium.copy(fontFamily = LexendMega),
            displaySmall = displaySmall.copy(fontFamily = LexendMega),
        )
    }

@Composable
private fun createTypographyWith(family: FontFamily): Typography {
    val base = Typography()
    return Typography().copy(
        displayLarge = base.displayLarge.copy(fontFamily = family, letterSpacing = (-7).sp),
        displayMedium = base.displayMedium.copy(fontFamily = family, letterSpacing = (-5).sp),
        displaySmall = base.displaySmall.copy(fontFamily = family, letterSpacing = (-5).sp),
        headlineLarge = base.headlineLarge.copy(fontFamily = family),
        headlineMedium = base.headlineMedium.copy(fontFamily = family),
        headlineSmall = base.headlineSmall.copy(fontFamily = family),
        titleLarge = base.titleLarge.copy(fontFamily = family),
        titleMedium = base.titleMedium.copy(fontFamily = family),
        titleSmall = base.titleSmall.copy(fontFamily = family),
        bodyLarge = base.bodyLarge.copy(fontFamily = family),
        bodyMedium = base.bodyMedium.copy(fontFamily = family),
        bodySmall = base.bodySmall.copy(fontFamily = family),
        labelLarge = base.labelLarge.copy(fontFamily = family),
        labelMedium = base.labelMedium.copy(fontFamily = family),
        labelSmall = base.labelSmall.copy(fontFamily = family),
    )
}
