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
public val Lucide.Smartphone: ImageVector
    get() {
        if (smartphone != null) {
            return smartphone!!
        }
        smartphone = ImageVector
            .Builder(
                name = "Smartphone",
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
                    moveTo(7f, 2f)
                    horizontalLineTo(17f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19f, 4f)
                    verticalLineTo(20f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 17f, 22f)
                    horizontalLineTo(7f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 20f)
                    verticalLineTo(4f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7f, 2f)
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
                    moveTo(12f, 18f)
                    horizontalLineToRelative(0.01f)
                }
            }.build()
        return smartphone!!
    }

private var smartphone: ImageVector? = null
