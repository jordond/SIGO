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
public val Lucide.Waves: ImageVector
    get() {
        if (waves != null) {
            return waves!!
        }
        waves = ImageVector
            .Builder(
                name = "Waves",
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
                    moveTo(2f, 6f)
                    curveToRelative(0.6f, 0.5f, 1.2f, 1f, 2.5f, 1f)
                    curveTo(7f, 7f, 7f, 5f, 9.5f, 5f)
                    curveToRelative(2.6f, 0f, 2.4f, 2f, 5f, 2f)
                    curveToRelative(2.5f, 0f, 2.5f, -2f, 5f, -2f)
                    curveToRelative(1.3f, 0f, 1.9f, 0.5f, 2.5f, 1f)
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
                    moveTo(2f, 12f)
                    curveToRelative(0.6f, 0.5f, 1.2f, 1f, 2.5f, 1f)
                    curveToRelative(2.5f, 0f, 2.5f, -2f, 5f, -2f)
                    curveToRelative(2.6f, 0f, 2.4f, 2f, 5f, 2f)
                    curveToRelative(2.5f, 0f, 2.5f, -2f, 5f, -2f)
                    curveToRelative(1.3f, 0f, 1.9f, 0.5f, 2.5f, 1f)
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
                    moveTo(2f, 18f)
                    curveToRelative(0.6f, 0.5f, 1.2f, 1f, 2.5f, 1f)
                    curveToRelative(2.5f, 0f, 2.5f, -2f, 5f, -2f)
                    curveToRelative(2.6f, 0f, 2.4f, 2f, 5f, 2f)
                    curveToRelative(2.5f, 0f, 2.5f, -2f, 5f, -2f)
                    curveToRelative(1.3f, 0f, 1.9f, 0.5f, 2.5f, 1f)
                }
            }.build()
        return waves!!
    }

private var waves: ImageVector? = null
