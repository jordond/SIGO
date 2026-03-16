package now.shouldigooutside.core.ui

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

internal val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
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
    staticCompositionLocalOf { Shapes }
