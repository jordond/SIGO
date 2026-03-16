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
public val Lucide.Handshake: ImageVector
    get() {
        if (handshake != null) {
            return handshake!!
        }
        handshake = ImageVector
            .Builder(
                name = "Handshake",
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
                    moveTo(11f, 17f)
                    lineToRelative(2f, 2f)
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 3f, -3f)
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
                    moveTo(14f, 14f)
                    lineToRelative(2.5f, 2.5f)
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 3f, -3f)
                    lineToRelative(-3.88f, -3.88f)
                    arcToRelative(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4.24f, 0f)
                    lineToRelative(-0.88f, 0.88f)
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = true, -3f, -3f)
                    lineToRelative(2.81f, -2.81f)
                    arcToRelative(
                        5.79f,
                        5.79f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        7.06f,
                        -0.87f,
                    )
                    lineToRelative(0.47f, 0.28f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.42f, 0.25f)
                    lineTo(21f, 4f)
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
                    moveTo(21f, 3f)
                    lineToRelative(1f, 11f)
                    horizontalLineToRelative(-2f)
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
                    moveTo(3f, 3f)
                    lineTo(2f, 14f)
                    lineToRelative(6.5f, 6.5f)
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 3f, -3f)
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
                    moveTo(3f, 4f)
                    horizontalLineToRelative(8f)
                }
            }.build()
        return handshake!!
    }

private var handshake: ImageVector? = null
