package now.shouldigooutside.core.ui.icons.tabler

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val Tabler.Swim: ImageVector
    get() {
        if (swim != null) return swim!!

        swim = ImageVector
            .Builder(
                name = "swimming",
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
                    moveTo(16f, 9f)
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
                    moveTo(6f, 11f)
                    lineToRelative(4f, -2f)
                    lineToRelative(3.5f, 3f)
                    lineToRelative(-1.5f, 2f)
                }
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(3f, 16.75f)
                    arcToRelative(2.4f, 2.4f, 0f, false, false, 1f, 0.25f)
                    arcToRelative(2.4f, 2.4f, 0f, false, false, 2f, -1f)
                    arcToRelative(2.4f, 2.4f, 0f, false, true, 2f, -1f)
                    arcToRelative(2.4f, 2.4f, 0f, false, true, 2f, 1f)
                    arcToRelative(2.4f, 2.4f, 0f, false, false, 2f, 1f)
                    arcToRelative(2.4f, 2.4f, 0f, false, false, 2f, -1f)
                    arcToRelative(2.4f, 2.4f, 0f, false, true, 2f, -1f)
                    arcToRelative(2.4f, 2.4f, 0f, false, true, 2f, 1f)
                    arcToRelative(2.4f, 2.4f, 0f, false, false, 2f, 1f)
                    arcToRelative(2.4f, 2.4f, 0f, false, false, 1f, -0.25f)
                }
            }.build()

        return swim!!
    }

private var swim: ImageVector? = null
