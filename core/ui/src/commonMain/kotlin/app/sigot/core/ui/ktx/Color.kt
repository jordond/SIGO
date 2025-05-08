package app.sigot.core.ui.ktx

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.ContrastThreshold
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.hasEnoughContrast
import com.materialkolor.ktx.isLight
import com.materialkolor.ktx.lighten

internal const val DISABLED_ALPHA = 0.38f

public fun Color.disabled(alpha: Float = DISABLED_ALPHA): Color = copy(alpha = alpha)

@Composable
public fun Color.withContrast(
    background: Color,
    calculate: Boolean,
    fallback: Color = MaterialTheme.colorScheme.inverseOnSurface,
    contrastThreshold: ContrastThreshold = ContrastThreshold.WCAG_AAA_NORMAL_TEXT,
): Color {
    if (calculate) return withContrastCalculateFallback(background, contrastThreshold)
    return withContrast(background, fallback, contrastThreshold)
}

@Composable
public fun Color.withContrast(
    background: Color,
    fallback: Color = MaterialTheme.colorScheme.inverseOnSurface,
    contrastThreshold: ContrastThreshold = ContrastThreshold.WCAG_AAA_NORMAL_TEXT,
): Color =
    if (hasEnoughContrast(background, contrastThreshold)) {
        this
    } else {
        val contentColor = contentColorFor(background)
        if (contentColor == this) {
            fallback
        } else {
            contentColor.withContrast(background, fallback, contrastThreshold)
        }
    }

@Composable
public fun Color.withContrastCalculateFallback(
    background: Color,
    contrastThreshold: ContrastThreshold = ContrastThreshold.WCAG_AAA_NORMAL_TEXT,
    count: Int = 0,
): Color {
    return if (hasEnoughContrast(background, contrastThreshold)) {
        this
    } else {
        val fallback = if (background.isLight()) darken(1.5f) else lighten(1.1f)
        if (count >= 20) return fallback
        if (fallback == Color.Black || fallback == Color.White) return fallback
        fallback.withContrastCalculateFallback(background, contrastThreshold, count = count + 1)
    }
}
