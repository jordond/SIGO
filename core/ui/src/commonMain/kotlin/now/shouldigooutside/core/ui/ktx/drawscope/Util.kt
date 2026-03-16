package now.shouldigooutside.core.ui.ktx.drawscope

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private inline fun <reified C : Corner> Set<Corner>.dp(radius: Dp): Dp =
    if (filterIsInstance<C>().isNotEmpty()) radius / 2 else 0.dp

public fun Set<Corner>.roundedCornerShape(radius: Dp): RoundedCornerShape =
    RoundedCornerShape(
        topStart = dp<Corner.TopLeft>(radius),
        topEnd = dp<Corner.TopRight>(radius),
        bottomStart = dp<Corner.BottomLeft>(radius),
        bottomEnd = dp<Corner.BottomRight>(radius),
    )
