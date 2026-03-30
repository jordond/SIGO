package now.shouldigooutside.core.ui.icons.phosphor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val Phosphor.Bicycle: ImageVector
    get() {
        if (bicycle != null) return bicycle!!

        bicycle = ImageVector
            .Builder(
                name = "bicycle",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 256f,
                viewportHeight = 256f,
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                ) {
                    moveTo(208f, 112f)
                    arcToRelative(47.81f, 47.81f, 0f, false, false, -16.93f, 3.09f)
                    lineTo(165.93f, 72f)
                    horizontalLineTo(192f)
                    arcToRelative(8f, 8f, 0f, false, true, 8f, 8f)
                    arcToRelative(8f, 8f, 0f, false, false, 16f, 0f)
                    arcToRelative(24f, 24f, 0f, false, false, -24f, -24f)
                    horizontalLineTo(152f)
                    arcToRelative(8f, 8f, 0f, false, false, -6.91f, 12f)
                    lineToRelative(11.65f, 20f)
                    horizontalLineTo(99.26f)
                    lineTo(82.91f, 60f)
                    arcTo(8f, 8f, 0f, false, false, 76f, 56f)
                    horizontalLineTo(48f)
                    arcToRelative(8f, 8f, 0f, false, false, 0f, 16f)
                    horizontalLineTo(71.41f)
                    lineTo(85.12f, 95.51f)
                    lineTo(69.41f, 117.06f)
                    arcToRelative(48.13f, 48.13f, 0f, true, false, 12.92f, 9.44f)
                    lineToRelative(11.59f, -15.9f)
                    lineTo(125.09f, 164f)
                    arcTo(8f, 8f, 0f, true, false, 138.91f, 156f)
                    lineToRelative(-30.32f, -52f)
                    horizontalLineToRelative(57.48f)
                    lineToRelative(11.19f, 19.17f)
                    arcTo(48f, 48f, 0f, true, false, 208f, 112f)
                    close()
                    moveTo(80f, 160f)
                    arcToRelative(32f, 32f, 0f, true, true, -20.21f, -29.74f)
                    lineToRelative(-18.25f, 25f)
                    arcToRelative(8f, 8f, 0f, true, false, 12.92f, 9.42f)
                    lineToRelative(18.25f, -25f)
                    arcTo(31.88f, 31.88f, 0f, false, true, 80f, 160f)
                    close()
                    moveToRelative(128f, 32f)
                    arcToRelative(32f, 32f, 0f, false, true, -22.51f, -54.72f)
                    lineTo(201.09f, 164f)
                    arcTo(8f, 8f, 0f, true, false, 214.91f, 156f)
                    lineTo(199.3f, 129.21f)
                    arcTo(32f, 32f, 0f, true, true, 208f, 192f)
                    close()
                }
            }.build()

        return bicycle!!
    }

private var bicycle: ImageVector? = null
