package now.shouldigooutside.core.ui.ktx

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Paint

public fun Canvas.drawRoundRect(
    rect: Rect,
    cornerRadius: Float,
    paint: Paint,
) {
    drawRoundRect(
        left = rect.left,
        top = rect.top,
        right = rect.right,
        bottom = rect.bottom,
        radiusX = cornerRadius,
        radiusY = cornerRadius,
        paint = paint,
    )
}
