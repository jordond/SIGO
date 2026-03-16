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
public val Lucide.MapPin: ImageVector
    get() {
        if (mapPin != null) {
            return mapPin!!
        }
        mapPin = ImageVector
            .Builder(
                name = "MapPin",
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
                    moveTo(20f, 10f)
                    curveToRelative(0f, 4.993f, -5.539f, 10.193f, -7.399f, 11.799f)
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.202f, 0f)
                    curveTo(9.539f, 20.193f, 4f, 14.993f, 4f, 10f)
                    arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16f, 0f)
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
                    moveTo(15f, 10f)
                    arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 13f)
                    arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 10f)
                    arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15f, 10f)
                    close()
                }
            }.build()
        return mapPin!!
    }

private var mapPin: ImageVector? = null
