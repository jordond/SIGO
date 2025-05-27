package app.sigot.core.ui.icons.lucide

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val Lucide.Vibrate: ImageVector
    get() {
        if (vibrate != null) {
            return vibrate!!
        }
        vibrate = ImageVector
            .Builder(
                name = "Vibrate",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(2f, 8f)
                    lineToRelative(2f, 2f)
                    lineToRelative(-2f, 2f)
                    lineToRelative(2f, 2f)
                    lineToRelative(-2f, 2f)
                }
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(22f, 8f)
                    lineToRelative(-2f, 2f)
                    lineToRelative(2f, 2f)
                    lineToRelative(-2f, 2f)
                    lineToRelative(2f, 2f)
                }
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(9f, 5f)
                    horizontalLineTo(15f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16f, 6f)
                    verticalLineTo(18f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15f, 19f)
                    horizontalLineTo(9f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 18f)
                    verticalLineTo(6f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 5f)
                    close()
                }
            }.build()
        return vibrate!!
    }

private var vibrate: ImageVector? = null
