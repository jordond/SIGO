package now.shouldigooutside.core.ui.icons.lucide

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val Lucide.Server: ImageVector
    get() {
        if (server != null) {
            return server!!
        }
        server = ImageVector
            .Builder(
                name = "Server",
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
                    moveTo(4f, 2f)
                    horizontalLineTo(20f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 4f)
                    verticalLineTo(8f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 10f)
                    horizontalLineTo(4f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 8f)
                    verticalLineTo(4f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 2f)
                    close()
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
                    moveTo(4f, 14f)
                    horizontalLineTo(20f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 16f)
                    verticalLineTo(20f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 22f)
                    horizontalLineTo(4f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 20f)
                    verticalLineTo(16f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 14f)
                    close()
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
                    moveTo(6f, 6f)
                    lineTo(6.01f, 6f)
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
                    moveTo(6f, 18f)
                    lineTo(6.01f, 18f)
                }
            }.build()
        return server!!
    }

private var server: ImageVector? = null
