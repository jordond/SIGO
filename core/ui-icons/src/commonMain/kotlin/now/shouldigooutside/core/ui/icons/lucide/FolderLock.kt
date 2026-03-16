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
public val Lucide.FolderLock: ImageVector
    get() {
        if (folderLock != null) {
            return folderLock!!
        }
        folderLock = ImageVector
            .Builder(
                name = "FolderLock",
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
                    moveTo(15f, 17f)
                    horizontalLineTo(21f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22f, 18f)
                    verticalLineTo(21f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 22f)
                    horizontalLineTo(15f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14f, 21f)
                    verticalLineTo(18f)
                    arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15f, 17f)
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
                    moveTo(10f, 20f)
                    horizontalLineTo(4f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
                    verticalLineTo(5f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                    horizontalLineToRelative(3.9f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1.69f, 0.9f)
                    lineToRelative(0.81f, 1.2f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 1.67f, 0.9f)
                    horizontalLineTo(20f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
                    verticalLineToRelative(2.5f)
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
                    moveTo(20f, 17f)
                    verticalLineToRelative(-2f)
                    arcToRelative(2f, 2f, 0f, isMoreThanHalf = true, isPositiveArc = false, -4f, 0f)
                    verticalLineToRelative(2f)
                }
            }.build()
        return folderLock!!
    }

private var folderLock: ImageVector? = null
