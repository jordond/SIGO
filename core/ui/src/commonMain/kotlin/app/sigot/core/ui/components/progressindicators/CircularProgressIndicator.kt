package app.sigot.core.ui.components.progressindicators

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max

@Composable
public fun CircularProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = CircularProgressIndicatorDefaults.Color,
    trackColor: Color = CircularProgressIndicatorDefaults.TrackColor,
    strokeWidth: Dp = CircularProgressIndicatorDefaults.StrokeWidth,
    strokeCap: StrokeCap = CircularProgressIndicatorDefaults.StrokeStyle,
) {
    val coercedProgress = { progress().coerceIn(0f, 1f) }
    val stroke =
        with(LocalDensity.current) {
            Stroke(width = strokeWidth.toPx(), cap = strokeCap)
        }
    Canvas(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(coercedProgress(), 0f..1f)
            }.size(CircularProgressIndicatorDefaults.Diameter),
    ) {
        val startAngle = CircularProgressIndicatorDefaults.StartAngle
        val sweep = coercedProgress() * CircularProgressIndicatorDefaults.SweepAngle
        drawCircularIndicatorTrack(trackColor, stroke)
        drawDeterminateCircularIndicator(startAngle, sweep, color, stroke)
    }
}

@Composable
public fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = CircularProgressIndicatorDefaults.Color,
    trackColor: Color = CircularProgressIndicatorDefaults.TrackColor,
    strokeWidth: Dp = CircularProgressIndicatorDefaults.StrokeWidth,
    strokeCap: StrokeCap = CircularProgressIndicatorDefaults.StrokeStyle,
) {
    val stroke =
        with(LocalDensity.current) {
            Stroke(width = strokeWidth.toPx(), cap = strokeCap)
        }

    val transition = rememberInfiniteTransition(label = "Indeterminate Transition")
    val currentRotation =
        transition.animateValue(
            initialValue = 0,
            targetValue = CircularProgressIndicatorDefaults.RotationsPerCycle,
            typeConverter = Int.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation =
                    tween(
                        durationMillis =
                            CircularProgressIndicatorDefaults.RotationDuration *
                                CircularProgressIndicatorDefaults.RotationsPerCycle,
                        easing = LinearEasing,
                    ),
            ),
            label = "Current Rotation",
        )
    val baseRotation =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = CircularProgressIndicatorDefaults.BaseRotationAngle,
            animationSpec = infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = CircularProgressIndicatorDefaults.RotationDuration,
                        easing = LinearEasing,
                    ),
            ),
            label = "Base Rotation",
        )
    val endAngle =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = CircularProgressIndicatorDefaults.JumpRotationAngle,
            animationSpec = infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = CircularProgressIndicatorDefaults.HeadTailAnimationDuration +
                            CircularProgressIndicatorDefaults.HeadTailDelayDuration
                        0f at 0 using CircularProgressIndicatorDefaults.CircularEasing
                        CircularProgressIndicatorDefaults.JumpRotationAngle at
                            CircularProgressIndicatorDefaults.HeadTailAnimationDuration
                    },
            ),
            label = "End Angle",
        )
    val startAngle =
        transition.animateFloat(
            initialValue = 0f,
            targetValue = CircularProgressIndicatorDefaults.JumpRotationAngle,
            animationSpec = infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = CircularProgressIndicatorDefaults.HeadTailAnimationDuration +
                            CircularProgressIndicatorDefaults.HeadTailDelayDuration
                        0f at CircularProgressIndicatorDefaults.HeadTailDelayDuration using
                            CircularProgressIndicatorDefaults.CircularEasing
                        CircularProgressIndicatorDefaults.JumpRotationAngle at durationMillis
                    },
            ),
            label = "Start Angle",
        )
    Canvas(
        modifier = modifier
            .progressSemantics()
            .size(CircularProgressIndicatorDefaults.Diameter),
    ) {
        drawCircularIndicatorTrack(trackColor, stroke)

        val currentRotationAngleOffset =
            (currentRotation.value * CircularProgressIndicatorDefaults.RotationAngleOffset) % 360f

        val sweep = abs(endAngle.value - startAngle.value)

        val offset =
            CircularProgressIndicatorDefaults.StartAngleOffset +
                currentRotationAngleOffset + baseRotation.value
        drawIndeterminateCircularIndicator(
            startAngle = startAngle.value + offset,
            strokeWidth = strokeWidth,
            sweep = sweep,
            color = color,
            stroke = stroke,
        )
    }
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke,
) {
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke,
    )
}

private fun DrawScope.drawCircularIndicatorTrack(
    color: Color,
    stroke: Stroke,
) = drawCircularIndicator(0f, CircularProgressIndicatorDefaults.SweepAngle, color, stroke)

private fun DrawScope.drawDeterminateCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke,
) = drawCircularIndicator(startAngle, sweep, color, stroke)

private fun DrawScope.drawIndeterminateCircularIndicator(
    startAngle: Float,
    strokeWidth: Dp,
    sweep: Float,
    color: Color,
    stroke: Stroke,
) {
    val strokeCapOffset =
        if (stroke.cap == StrokeCap.Butt) {
            0f
        } else {
            (180.0 / PI).toFloat() * (strokeWidth / (CircularProgressIndicatorDefaults.Diameter / 2)) / 2f
        }

    val adjustedStartAngle = startAngle + strokeCapOffset

    val adjustedSweep = max(sweep, 0.1f)

    drawCircularIndicator(adjustedStartAngle, adjustedSweep, color, stroke)
}

@Suppress("ConstPropertyName")
public object CircularProgressIndicatorDefaults {
    public val Color: Color
        @Composable get() = AppTheme.colors.primary

    public val TrackColor: Color
        @Composable get() = AppTheme.colors.transparent

    private val Size = 48.dp
    private val ActiveIndicatorWidth = 2.dp
    public val Diameter: Dp = Size - ActiveIndicatorWidth * 2

    public val StrokeWidth: Dp = 4.dp
    public val StrokeStyle: StrokeCap = StrokeCap.Square
    public const val RotationsPerCycle: Int = 5
    public const val RotationDuration: Int = 1332
    public const val StartAngleOffset: Float = -90f
    public const val BaseRotationAngle: Float = 286f
    public const val JumpRotationAngle: Float = 290f
    public const val RotationAngleOffset: Float = (BaseRotationAngle + JumpRotationAngle) % 360f
    public const val SweepAngle: Float = 360f
    public const val StartAngle: Float = 270f
    public const val HeadTailAnimationDuration: Int = (RotationDuration * 0.5).toInt()
    public const val HeadTailDelayDuration: Int = HeadTailAnimationDuration

    public val CircularEasing: CubicBezierEasing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
}

@Composable
@Preview
internal fun CircularProgressIndicatorPreview() {
    AppPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Determinate Progress",
                style = AppTheme.typography.body1,
            )
            CircularProgressIndicator(progress = { 0.7f })

            Text(
                text = "Indeterminate Progress",
                style = AppTheme.typography.body1,
            )
            CircularProgressIndicator()
        }
    }
}
