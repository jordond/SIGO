package now.shouldigooutside.core.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public data class Spacing(
    val mini: Dp = 4.dp,
    val small: Dp = 8.dp,
    val standard: Dp = 16.dp,
    val large: Dp = 32.dp,
)

public val LocalSpacing: ProvidableCompositionLocal<Spacing> = compositionLocalOf { Spacing() }
