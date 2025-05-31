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
public val Lucide.ArrowBigDown: ImageVector
    get() {
        if (arrowBigDown != null) {
            return arrowBigDown!!
        }
        arrowBigDown = ImageVector
            .Builder(
                name = "ArrowBigDown",
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
                    moveTo(15f, 6f)
                    verticalLineToRelative(6f)
                    horizontalLineToRelative(4f)
                    lineToRelative(-7f, 7f)
                    lineToRelative(-7f, -7f)
                    horizontalLineToRelative(4f)
                    verticalLineTo(6f)
                    horizontalLineToRelative(6f)
                    close()
                }
            }.build()
        return arrowBigDown!!
    }

private var arrowBigDown: ImageVector? = null
