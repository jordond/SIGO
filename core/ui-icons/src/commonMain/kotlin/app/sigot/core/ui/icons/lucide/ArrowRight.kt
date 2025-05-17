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
public val Lucide.ArrowRight: ImageVector
    get() {
        if (arrowRight != null) {
            return arrowRight!!
        }
        arrowRight = ImageVector
            .Builder(
                name = "ArrowRight",
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
                    moveTo(5f, 12f)
                    horizontalLineToRelative(14f)
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
                    moveTo(12f, 5f)
                    lineToRelative(7f, 7f)
                    lineToRelative(-7f, 7f)
                }
            }.build()
        return arrowRight!!
    }

private var arrowRight: ImageVector? = null
