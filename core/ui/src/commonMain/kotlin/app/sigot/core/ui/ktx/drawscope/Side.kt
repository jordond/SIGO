package app.sigot.core.ui.ktx.drawscope

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

@Immutable
public interface Side {
    public data object Top : Side

    public data object Bottom : Side

    public data object Left : Side

    public data object Right : Side
}

public fun DrawScope.drawSide(
    side: Side,
    color: Color,
    strokeWidth: Float,
    radius: Float = 0f,
    cornersToOffset: Set<Corner> = emptySet(),
) {
    when (side) {
        Side.Top -> drawTopSide(color, strokeWidth, radius, cornersToOffset)
        Side.Bottom -> drawBottomSide(color, strokeWidth, radius, cornersToOffset)
        Side.Left -> drawLeftSide(color, strokeWidth, radius, cornersToOffset)
        Side.Right -> drawRightSide(color, strokeWidth, radius, cornersToOffset)
    }
}

private fun DrawScope.drawSide(
    start: Offset,
    end: Offset,
    color: Color = Color.Black,
    strokeWidth: Float,
) {
    drawLine(color = color, start = start, end = end, strokeWidth = strokeWidth)
}

private fun DrawScope.drawTopSide(
    color: Color,
    width: Float,
    radius: Float,
    cornersToOffset: Set<Corner>,
) {
    val topLeftOffset = if (Corner.TopLeft in cornersToOffset) radius else 0f
    val topRightOffset = if (Corner.TopRight in cornersToOffset) radius else 0f
    drawSide(
        start = Offset(topLeftOffset, 0f),
        end = Offset(size.width - topRightOffset, 0f),
        color = color,
        strokeWidth = width,
    )
}

private fun DrawScope.drawBottomSide(
    color: Color,
    width: Float,
    radius: Float,
    cornersToOffset: Set<Corner>,
) {
    val bottomLeftOffset = if (Corner.BottomLeft in cornersToOffset) radius else 0f
    val bottomRightOffset = if (Corner.BottomRight in cornersToOffset) radius else 0f
    drawSide(
        start = Offset(bottomLeftOffset, size.height),
        end = Offset(size.width - bottomRightOffset, size.height),
        color = color,
        strokeWidth = width,
    )
}

private fun DrawScope.drawLeftSide(
    color: Color,
    width: Float,
    radius: Float,
    cornersToOffset: Set<Corner>,
) {
    val topLeftOffset = if (Corner.TopLeft in cornersToOffset) radius else 0f
    val bottomLeftOffset = if (Corner.BottomLeft in cornersToOffset) radius else 0f
    drawSide(
        start = Offset(0f, topLeftOffset),
        end = Offset(0f, size.height - bottomLeftOffset),
        color = color,
        strokeWidth = width,
    )
}

private fun DrawScope.drawRightSide(
    color: Color,
    width: Float,
    radius: Float,
    cornersToOffset: Set<Corner>,
) {
    val topRightOffset = if (Corner.TopRight in cornersToOffset) radius else 0f
    val bottomRightOffset = if (Corner.BottomRight in cornersToOffset) radius else 0f
    drawSide(
        start = Offset(size.width, topRightOffset),
        end = Offset(size.width, size.height - bottomRightOffset),
        color = color,
        strokeWidth = width,
    )
}
