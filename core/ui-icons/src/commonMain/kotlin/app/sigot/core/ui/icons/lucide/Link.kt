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
public val Lucide.Link: ImageVector
    get() {
        if (link != null) {
            return link!!
        }
        link = ImageVector
            .Builder(
                name = "Link",
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
                    moveTo(10f, 13f)
                    arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7.54f, 0.54f)
                    lineToRelative(3f, -3f)
                    arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -7.07f, -7.07f)
                    lineToRelative(-1.72f, 1.71f)
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
                    moveTo(14f, 11f)
                    arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = false, -7.54f, -0.54f)
                    lineToRelative(-3f, 3f)
                    arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7.07f, 7.07f)
                    lineToRelative(1.71f, -1.71f)
                }
            }.build()
        return link!!
    }

private var link: ImageVector? = null
