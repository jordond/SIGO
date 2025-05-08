package app.sigot.core.ui.ktx

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toPersistentList

@Composable
public fun Modifier.animatedBorder(
    color: Color,
    backgroundColor: Color,
    shape: Shape = MaterialTheme.shapes.large,
    width: Dp = 2.dp,
    animationDurationInMillis: Int = 1000,
    easing: Easing = LinearEasing,
): Modifier =
    this.animatedBorder(
        colors = listOf(color),
        backgroundColor = backgroundColor,
        shape = shape,
        width = width,
        animationDurationInMillis = animationDurationInMillis,
        easing = easing,
        animate = false,
        stillColor = color,
        mirror = false,
    )

@Composable
public fun Modifier.animatedBorder(
    colors: List<Color>,
    backgroundColor: Color,
    shape: Shape = MaterialTheme.shapes.large,
    width: Dp = 2.dp,
    animationDurationInMillis: Int = 1000,
    easing: Easing = LinearEasing,
    animate: Boolean = true,
    stillColor: Color = colors.first(),
    mirror: Boolean = true,
): Modifier {
    val borderColors = remember(colors, mirror) {
        if (mirror) (colors + colors.reversed()).toPersistentList() else colors
    }

    val brush = remember(borderColors, animate) {
        when {
            borderColors.size == 1 -> SolidColor(borderColors.first())
            animate -> Brush.sweepGradient(borderColors)
            else -> SolidColor(stillColor)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "animatedBorder")
    val angle =
        if (!animate || borderColors.size == 1) {
            0f
        } else {
            infiniteTransition
                .animateFloat(
                    label = "angleAnimation",
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = animationDurationInMillis,
                            easing = easing,
                        ),
                        repeatMode = RepeatMode.Restart,
                    ),
                ).value
        }

    return this
        .clip(shape)
        .padding(width)
        .drawWithContent {
            rotate(angle) {
                drawCircle(
                    brush = brush,
                    radius = size.width * 1.1f,
                    blendMode = BlendMode.SrcIn,
                )
            }
            drawContent()
        }.background(color = backgroundColor, shape = shape)
}
