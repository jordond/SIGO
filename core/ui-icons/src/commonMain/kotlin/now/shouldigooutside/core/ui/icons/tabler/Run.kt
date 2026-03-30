package now.shouldigooutside.core.ui.icons.tabler

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val Tabler.Run: ImageVector
    get() {
        if (run != null) return run!!

        run = ImageVector
            .Builder(
                name = "run",
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
                    moveTo(13f, 4f)
                    moveToRelative(-1f, 0f)
                    arcToRelative(1f, 1f, 0f, true, false, 2f, 0f)
                    arcToRelative(1f, 1f, 0f, true, false, -2f, 0f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(4f, 17f)
                    lineToRelative(5f, 1f)
                    lineToRelative(0.75f, -1.5f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(15f, 21f)
                    lineToRelative(0f, -4f)
                    lineToRelative(-4f, -3f)
                    lineToRelative(1f, -6f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(7f, 12f)
                    lineToRelative(0f, -3f)
                    lineToRelative(5f, -1f)
                    lineToRelative(3f, 3f)
                    lineToRelative(3f, 1f)
                }
            }.build()

        return run!!
    }

private var run: ImageVector? = null
