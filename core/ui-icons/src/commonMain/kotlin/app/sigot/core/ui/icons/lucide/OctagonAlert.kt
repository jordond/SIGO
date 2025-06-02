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
public val Lucide.OctagonAlert: ImageVector
    get() {
        if (octagonAlert != null) {
            return octagonAlert!!
        }
        octagonAlert = ImageVector
            .Builder(
                name = "OctagonAlert",
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
                    moveTo(12f, 16f)
                    horizontalLineToRelative(0.01f)
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
                    moveTo(12f, 8f)
                    verticalLineToRelative(4f)
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
                    moveTo(15.312f, 2f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.414f, 0.586f)
                    lineToRelative(4.688f, 4.688f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 8.688f)
                    verticalLineToRelative(6.624f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -0.586f, 1.414f)
                    lineToRelative(-4.688f, 4.688f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.414f, 0.586f)
                    horizontalLineTo(8.688f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -1.414f, -0.586f)
                    lineToRelative(-4.688f, -4.688f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 15.312f)
                    verticalLineTo(8.688f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0.586f, -1.414f)
                    lineToRelative(4.688f, -4.688f)
                    arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.688f, 2f)
                    close()
                }
            }.build()
        return octagonAlert!!
    }

private var octagonAlert: ImageVector? = null
