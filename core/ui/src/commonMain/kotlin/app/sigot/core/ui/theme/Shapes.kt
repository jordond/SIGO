@file:Suppress("FunctionName")

package app.sigot.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

public object Shapes {
    public val InputPill: RoundedCornerShape = RoundedShape(
        topStart = AppRadius.Small,
        topEnd = AppRadius.Large,
        bottomStart = AppRadius.Large,
        bottomEnd = AppRadius.Small,
    )
}

public enum class AppRadius(
    public val value: Dp,
) {
    ExtraSmall(4.dp),
    Small(8.dp),
    MediumSmall(12.dp),
    Medium(16.dp),
    Large(24.dp),
    ExtraLarge(32.dp),
    ExtraExtraLarge(42.dp),
    None(0.dp),
}

public fun RoundedShape(
    radius: AppRadius = AppRadius.Medium,
    top: Boolean = true,
    bottom: Boolean = true,
): RoundedCornerShape =
    RoundedCornerShape(
        topStart = if (top) radius.value else 0.dp,
        topEnd = if (top) radius.value else 0.dp,
        bottomStart = if (bottom) radius.value else 0.dp,
        bottomEnd = if (bottom) radius.value else 0.dp,
    )

public fun RoundedShape(
    start: AppRadius,
    end: AppRadius,
): RoundedCornerShape =
    RoundedCornerShape(
        topStart = start.value,
        topEnd = end.value,
        bottomStart = start.value,
        bottomEnd = end.value,
    )

public fun RoundedShape(
    topStart: AppRadius = AppRadius.None,
    topEnd: AppRadius = AppRadius.None,
    bottomStart: AppRadius = AppRadius.None,
    bottomEnd: AppRadius = AppRadius.None,
): RoundedCornerShape =
    RoundedCornerShape(
        topStart = topStart.value,
        topEnd = topEnd.value,
        bottomStart = bottomStart.value,
        bottomEnd = bottomEnd.value,
    )
