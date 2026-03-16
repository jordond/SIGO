package now.shouldigooutside.core.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
public fun Modifier.stroke(
    strokeWidth: Dp = 8.dp,
    strokeColor: Color = Color.White,
    strokeAlpha: Float = 1f,
    featheringLevels: PersistentList<Float> = persistentListOf(1.0f),
    edgeSmoothness: Int = 32,
): Modifier =
    this.drawWithCache {
        // Precompute the stroke half-width in pixels.
        val strokePx = strokeWidth.toPx() / 2f
        // Compute the offsets once.
        val computedOffsets = (0 until edgeSmoothness).map { i ->
            val angle = 2 * PI * i / edgeSmoothness
            Offset(
                x = (strokePx * cos(angle)).toFloat(),
                y = (strokePx * sin(angle)).toFloat(),
            )
        }
        // Create a paint object to reuse if possible.
        val paint = Paint().apply {
            isAntiAlias = true
            filterQuality = FilterQuality.High
        }

        onDrawWithContent {
            // For each feathering level and offset, draw the content with a tinted stroke.
            featheringLevels.forEach { feather ->
                computedOffsets.forEach { offset ->
                    withTransform({
                        translate(offset.x * feather, offset.y * feather)
                    }) {
                        drawIntoCanvas { canvas ->
                            // Update the paint's color filter.
                            paint.colorFilter = ColorFilter.tint(
                                strokeColor.copy(alpha = strokeAlpha * feather),
                                BlendMode.SrcIn,
                            )
                            val rect = Rect(Offset.Zero, size)
                            // Save the layer with our configured paint.
                            canvas.saveLayer(rect, paint)
                            this@onDrawWithContent.drawContent()
                            canvas.restore()
                        }
                    }
                }
            }
            // Draw the original content on top.
            drawContent()
        }
    }
