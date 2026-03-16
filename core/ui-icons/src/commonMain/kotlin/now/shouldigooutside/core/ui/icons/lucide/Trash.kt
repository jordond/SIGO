package now.shouldigooutside.core.ui.icons.lucide

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val Lucide.Trash: ImageVector
    get() {
        if (trash != null) return trash!!

        trash = ImageVector
            .Builder(
                name = "trash",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(19f, 6f)
                    verticalLineToRelative(14f)
                    arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                    horizontalLineTo(7f)
                    arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
                    verticalLineTo(6f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(3f, 6f)
                    horizontalLineToRelative(18f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(8f, 6f)
                    verticalLineTo(4f)
                    arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
                    horizontalLineToRelative(4f)
                    arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
                    verticalLineToRelative(2f)
                }
            }.build()

        return trash!!
    }

private var trash: ImageVector? = null
