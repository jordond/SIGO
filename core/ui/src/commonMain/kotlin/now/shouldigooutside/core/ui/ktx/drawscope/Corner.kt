package now.shouldigooutside.core.ui.ktx.drawscope

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

@Immutable
public sealed interface Corner {
    public data object TopLeft : Corner

    public data object TopRight : Corner

    public data object BottomLeft : Corner

    public data object BottomRight : Corner
}

public fun Corner.toRoundedShape(radius: Dp): RoundedCornerShape =
    when (this) {
        is Corner.TopLeft -> RoundedCornerShape(topStart = radius)
        is Corner.TopRight -> RoundedCornerShape(topEnd = radius)
        is Corner.BottomLeft -> RoundedCornerShape(bottomStart = radius)
        is Corner.BottomRight -> RoundedCornerShape(bottomEnd = radius)
    }

public fun DrawScope.drawRoundedCorner(
    corner: Corner,
    radius: Float,
    color: Color = Color.Black,
    strokeWidth: Float = 1f,
) {
    when (corner) {
        Corner.TopLeft -> drawRoundedTopLeftCorner(radius, color, strokeWidth)
        Corner.TopRight -> drawRoundedTopRight(radius, color, strokeWidth)
        Corner.BottomLeft -> drawRoundedBottomLeft(radius, color, strokeWidth)
        Corner.BottomRight -> drawRoundedBottomRight(radius, color, strokeWidth)
    }
}

private fun DrawScope.drawArc(
    radius: Float,
    color: Color,
    strokeWidth: Float,
    topLeft: Offset,
    startAngle: Float,
) {
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = 90f,
        useCenter = false,
        topLeft = topLeft,
        size = Size(width = radius * 2, height = radius * 2),
        style = Stroke(width = strokeWidth),
    )
}

private fun DrawScope.drawRoundedTopLeftCorner(
    radius: Float,
    color: Color,
    strokeWidth: Float,
) {
    val topLeft = Offset(0f, 0f)

    drawArc(
        radius = radius,
        color = color,
        startAngle = 180f,
        topLeft = topLeft,
        strokeWidth = strokeWidth,
    )
}

private fun DrawScope.drawRoundedTopRight(
    radius: Float,
    color: Color,
    strokeWidth: Float,
) {
    val topRight = Offset(size.width - radius * 2, 0f)

    drawArc(
        radius = radius,
        color = color,
        startAngle = 270f,
        topLeft = topRight,
        strokeWidth = strokeWidth,
    )
}

private fun DrawScope.drawRoundedBottomLeft(
    radius: Float,
    color: Color,
    strokeWidth: Float,
) {
    val bottomLeft = Offset(0f, size.height - radius * 2)

    drawArc(
        radius = radius,
        color = color,
        startAngle = 90f,
        topLeft = bottomLeft,
        strokeWidth = strokeWidth,
    )
}

private fun DrawScope.drawRoundedBottomRight(
    radius: Float,
    color: Color,
    strokeWidth: Float,
) {
    val bottomRight = Offset(size.width - radius * 2, size.height - radius * 2)

    drawArc(
        radius = radius,
        color = color,
        startAngle = 0f,
        topLeft = bottomRight,
        strokeWidth = strokeWidth,
    )
}
