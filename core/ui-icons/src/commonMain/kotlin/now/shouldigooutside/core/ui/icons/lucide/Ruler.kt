package now.shouldigooutside.core.ui.icons.lucide

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val Lucide.Ruler: ImageVector
    get() {
        if (ruler != null) {
            return ruler!!
        }
        ruler = ImageVector
            .Builder(
                name = "Ruler",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(12f, 15f)
                    verticalLineToRelative(-3.014f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(16f, 15f)
                    verticalLineToRelative(-3.014f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(20f, 6f)
                    horizontalLineTo(4f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(20f, 8f)
                    verticalLineTo(4f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(4f, 8f)
                    verticalLineTo(4f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(8f, 15f)
                    verticalLineToRelative(-3.014f)
                }
                path(
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(4f, 12f)
                    lineTo(20f, 12f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 13f)
                    lineTo(21f, 18f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 19f)
                    lineTo(4f, 19f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 18f)
                    lineTo(3f, 13f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 12f)
                    close()
                }
            }.build()

        return ruler!!
    }

private var ruler: ImageVector? = null
