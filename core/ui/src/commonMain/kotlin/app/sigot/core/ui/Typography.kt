package app.sigot.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
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

public val displayFont: FontFamily
    @Composable
    get() = LexendMega

public val contentFont: FontFamily
    @Composable
    get() = PublicSans

public val TextStyle.asDisplay: TextStyle
    @Composable
    get() = copy(fontFamily = displayFont)

public val TextStyle.asContent: TextStyle
    @Composable
    get() = copy(fontFamily = contentFont)

@Composable
public fun fontFamily(): FontFamily = contentFont

public data class Typography(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val label1: TextStyle,
    val label2: TextStyle,
    val label3: TextStyle,
    val button: TextStyle,
    val input: TextStyle,
)

private val defaultTypography =
    Typography(
        h1 =
            TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp,
            ),
        h2 =
            TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp,
            ),
        h3 =
            TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
        h4 =
            TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
        body1 =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
        body2 =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.15.sp,
            ),
        body3 =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.15.sp,
            ),
        label1 =
            TextStyle(
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        label2 =
            TextStyle(
                fontWeight = FontWeight.W500,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        label3 =
            TextStyle(
                fontWeight = FontWeight.W500,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                letterSpacing = 0.5.sp,
            ),
        button =
            TextStyle(
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 1.sp,
            ),
        input =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.sp,
            ),
    )

@Composable
public fun provideTypography(): Typography {
    val display = displayFont
    val content = contentFont
    return defaultTypography.copy(
        h1 = defaultTypography.h1.copy(fontFamily = display),
        h2 = defaultTypography.h2.copy(fontFamily = display),
        h3 = defaultTypography.h3.copy(fontFamily = display),
        h4 = defaultTypography.h4.copy(fontFamily = content),
        body1 = defaultTypography.body1.copy(fontFamily = content),
        body2 = defaultTypography.body2.copy(fontFamily = content),
        body3 = defaultTypography.body3.copy(fontFamily = content),
        label1 = defaultTypography.label1.copy(fontFamily = content),
        label2 = defaultTypography.label2.copy(fontFamily = content),
        label3 = defaultTypography.label3.copy(fontFamily = content),
        button = defaultTypography.button.copy(fontFamily = content),
        input = defaultTypography.input.copy(fontFamily = content),
    )
}

public val LocalTypography: ProvidableCompositionLocal<Typography> =
    staticCompositionLocalOf { defaultTypography }

public val LocalTextStyle: ProvidableCompositionLocal<TextStyle> =
    compositionLocalOf(structuralEqualityPolicy()) { TextStyle.Default }
