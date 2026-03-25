package now.shouldigooutside.core.ui

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

public data class Shapes(
    val extraSmall: CornerBasedShape = RoundedCornerShape(4.dp),
    val small: CornerBasedShape = RoundedCornerShape(8.dp),
    val medium: CornerBasedShape = RoundedCornerShape(12.dp),
    val large: CornerBasedShape = RoundedCornerShape(16.dp),
    val extraLarge: CornerBasedShape = RoundedCornerShape(24.dp),
)

public fun CornerBasedShape.rounded(
    top: Boolean = true,
    bottom: Boolean = true,
): CornerBasedShape =
    RoundedCornerShape(
        topStart = if (top) this.topStart else CornerSize(0.dp),
        topEnd = if (top) this.topEnd else CornerSize(0.dp),
        bottomStart = if (bottom) this.bottomStart else CornerSize(0.dp),
        bottomEnd = if (bottom) this.bottomEnd else CornerSize(0.dp),
    )

public val LocalShapes: ProvidableCompositionLocal<Shapes> =
    staticCompositionLocalOf { Shapes() }
