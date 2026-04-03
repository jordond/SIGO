package now.shouldigooutside.core.ui.icons.phosphor

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("UnusedReceiverParameter")
public val Phosphor.Hike: ImageVector
    get() {
        if (hike != null) return hike!!

        hike = ImageVector
            .Builder(
                name = "person-simple-hike",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 256f,
                viewportHeight = 256f,
            ).apply {
                path(
                    fill = SolidColor(Color.Black),
                ) {
                    moveTo(120f, 48f)
                    arcToRelative(32f, 32f, 0f, true, true, 32f, 32f)
                    arcTo(32f, 32f, 0f, false, true, 120f, 48f)
                    close()
                    moveToRelative(72f, 88f)
                    curveToRelative(-23.37f, 0f, -28.92f, -8.56f, -36.6f, -20.4f)
                    curveToRelative(-3.65f, -5.64f, -7.79f, -12f, -14.16f, -17.55f)
                    arcToRelative(40.92f, 40.92f, 0f, false, false, -8f, -5.47f)
                    arcToRelative(8f, 8f, 0f, false, false, -11f, 3.92f)
                    lineTo(64.66f, 228.81f)
                    arcToRelative(8f, 8f, 0f, false, false, 4.15f, 10.52f)
                    arcTo(7.84f, 7.84f, 0f, false, false, 72f, 240f)
                    arcToRelative(8f, 8f, 0f, false, false, 7.34f, -4.81f)
                    lineToRelative(33.59f, -77.27f)
                    lineTo(144f, 180.12f)
                    verticalLineTo(232f)
                    arcToRelative(8f, 8f, 0f, false, false, 16f, 0f)
                    verticalLineTo(176f)
                    arcToRelative(8f, 8f, 0f, false, false, -3.35f, -6.51f)
                    lineToRelative(-37.2f, -26.57f)
                    lineToRelative(13.4f, -30.81f)
                    curveToRelative(3.57f, 3.62f, 6.28f, 7.8f, 9.13f, 12.19f)
                    curveToRelative(7.67f, 11.84f, 16.27f, 25.11f, 42f, 27.36f)
                    verticalLineTo(232f)
                    arcToRelative(8f, 8f, 0f, false, false, 16f, 0f)
                    verticalLineTo(144f)
                    arcTo(8f, 8f, 0f, false, false, 192f, 136f)
                    close()
                    moveTo(72f, 152f)
                    arcToRelative(8f, 8f, 0f, false, false, 7.36f, -4.85f)
                    lineToRelative(24f, -56f)
                    arcToRelative(8f, 8f, 0f, false, false, -4.2f, -10.5f)
                    lineToRelative(-28f, -12f)
                    arcToRelative(8f, 8f, 0f, false, false, -10.5f, 4.2f)
                    lineToRelative(-24f, 56f)
                    arcToRelative(8f, 8f, 0f, false, false, 4.2f, 10.5f)
                    lineToRelative(28f, 12f)
                    arcTo(8f, 8f, 0f, false, false, 72f, 152f)
                    close()
                }
            }.build()

        return hike!!
    }

private var hike: ImageVector? = null
