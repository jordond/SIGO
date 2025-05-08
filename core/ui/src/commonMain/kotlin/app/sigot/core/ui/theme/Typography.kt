package app.sigot.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import app.toebean.core.resources.PatrickHandSC_Regular
import app.toebean.core.resources.Recoleta_Bold
import app.toebean.core.resources.Res
import org.jetbrains.compose.resources.Font

public val PatrickHandSc: FontFamily
    @Composable
    get() = FontFamily(Font(Res.font.PatrickHandSC_Regular))

public val Recoleta: FontFamily
    @Composable
    get() = FontFamily(Font(Res.font.Recoleta_Bold, weight = FontWeight.Bold))

public val displayFont: FontFamily
    @Composable
    get() = Recoleta

public val contentFont: FontFamily
    @Composable
    get() = PatrickHandSc

public val AppTypography: Typography
    @Composable get() = createTypographyWith(contentFont).run {
        copy(
            displayLarge = displayLarge.copy(fontFamily = displayFont),
            displayMedium = displayMedium.copy(fontFamily = displayFont),
            displaySmall = displaySmall.copy(fontFamily = displayFont),
        )
    }

public val TextStyle.asDisplay: TextStyle
    @Composable
    get() = copy(fontFamily = displayFont)

public val TextStyle.asContent: TextStyle
    @Composable
    get() = copy(fontFamily = contentFont)

@Composable
private fun createTypographyWith(family: FontFamily): Typography {
    val base = Typography()
    return Typography().copy(
        displayLarge = base.displayLarge.copy(fontFamily = family),
        displayMedium = base.displayMedium.copy(fontFamily = family),
        displaySmall = base.displaySmall.copy(fontFamily = family),
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
