package app.sigot.core.ui.components.progressindicators

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs

@Composable
public fun LinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = LinearProgressIndicatorDefaults.Color,
    trackColor: Color = LinearProgressIndicatorDefaults.TrackColor,
    strokeCap: StrokeCap = LinearProgressIndicatorDefaults.StrokeStyle,
) {
    androidx.compose.material3.LinearProgressIndicator({ 1f })
    val coercedProgress = { progress().coerceIn(0f, 1f) }
    Canvas(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                progressBarRangeInfo = ProgressBarRangeInfo(coercedProgress(), 0f..1f)
            }.height(LinearProgressIndicatorDefaults.TrackHeight)
            .fillMaxWidth(),
    ) {
        val strokeWidth = size.height
        drawLinearIndicatorTrack(trackColor, strokeWidth, strokeCap)
        drawLinearIndicator(0f, coercedProgress(), color, strokeWidth, strokeCap)
    }
}

@Composable
public fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = LinearProgressIndicatorDefaults.Color,
    trackColor: Color = LinearProgressIndicatorDefaults.TrackColor,
    strokeCap: StrokeCap = LinearProgressIndicatorDefaults.StrokeStyle,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "InfiniteTransition")
    val firstLineHead =
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = LinearProgressIndicatorDefaults.AnimationDuration
                        0f at LinearProgressIndicatorDefaults.FirstLineHeadDelay using
                            LinearProgressIndicatorDefaults.FirstLineHeadEasing
                        1f at
                            LinearProgressIndicatorDefaults.FirstLineHeadDuration +
                            LinearProgressIndicatorDefaults.FirstLineHeadDelay
                    },
            ),
            label = "FirstLineHead",
        )
    val firstLineTail =
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = LinearProgressIndicatorDefaults.AnimationDuration
                        0f at LinearProgressIndicatorDefaults.FirstLineTailDelay using
                            LinearProgressIndicatorDefaults.FirstLineTailEasing
                        1f at
                            LinearProgressIndicatorDefaults.FirstLineTailDuration +
                            LinearProgressIndicatorDefaults.FirstLineTailDelay
                    },
            ),
            label = "FirstLineTail",
        )
    val secondLineHead =
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = LinearProgressIndicatorDefaults.AnimationDuration
                        0f at LinearProgressIndicatorDefaults.SecondLineHeadDelay using
                            LinearProgressIndicatorDefaults.SecondLineHeadEasing
                        1f at
                            LinearProgressIndicatorDefaults.SecondLineHeadDuration +
                            LinearProgressIndicatorDefaults.SecondLineHeadDelay
                    },
            ),
            label = "SecondLineHead",
        )
    val secondLineTail =
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = LinearProgressIndicatorDefaults.AnimationDuration
                        0f at LinearProgressIndicatorDefaults.SecondLineTailDelay using
                            LinearProgressIndicatorDefaults.SecondLineTailEasing
                        1f at
                            LinearProgressIndicatorDefaults.SecondLineTailDuration +
                            LinearProgressIndicatorDefaults.SecondLineTailDelay
                    },
            ),
            label = "SecondLineTail",
        )
    Canvas(
        modifier = modifier
            .progressSemantics()
            .height(LinearProgressIndicatorDefaults.TrackHeight)
            .fillMaxWidth(),
    ) {
        val strokeWidth = size.height
        drawLinearIndicatorTrack(trackColor, strokeWidth, strokeCap)
        if (firstLineHead.value - firstLineTail.value > 0) {
            drawLinearIndicator(
                startFraction = firstLineHead.value,
                endFraction = firstLineTail.value,
                color = color,
                strokeWidth = strokeWidth,
                strokeCap = strokeCap,
            )
        }
        if (secondLineHead.value - secondLineTail.value > 0) {
            drawLinearIndicator(
                startFraction = secondLineHead.value,
                endFraction = secondLineTail.value,
                color = color,
                strokeWidth = strokeWidth,
                strokeCap = strokeCap,
            )
        }
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    val yOffset = height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width

    if (strokeCap == StrokeCap.Butt || height > width) {
        drawLine(color, Offset(barStart, yOffset), Offset(barEnd, yOffset), strokeWidth)
    } else {
        val strokeCapOffset = strokeWidth / 2
        val coerceRange = strokeCapOffset..(width - strokeCapOffset)
        val adjustedBarStart = barStart.coerceIn(coerceRange)
        val adjustedBarEnd = barEnd.coerceIn(coerceRange)

        if (abs(endFraction - startFraction) > 0) {
            drawLine(
                color = color,
                start = Offset(adjustedBarStart, yOffset),
                end = Offset(adjustedBarEnd, yOffset),
                strokeWidth = strokeWidth,
                cap = strokeCap,
            )
        }
    }
}

private fun DrawScope.drawLinearIndicatorTrack(
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) = drawLinearIndicator(0f, 1f, color, strokeWidth, strokeCap)

@Suppress("ConstPropertyName")
public object LinearProgressIndicatorDefaults {
    public val Color: Color
        @Composable get() = AppTheme.colors.primary

    public val TrackColor: Color
        @Composable get() = AppTheme.colors.transparent

    public val TrackHeight: Dp = 4.dp
    public val StrokeStyle: StrokeCap = StrokeCap.Round
    public const val AnimationDuration: Int = 1800

    public const val FirstLineHeadDuration: Int = 750
    public const val FirstLineTailDuration: Int = 850
    public const val SecondLineHeadDuration: Int = 567
    public const val SecondLineTailDuration: Int = 533

    public const val FirstLineHeadDelay: Int = 0
    public const val FirstLineTailDelay: Int = 333
    public const val SecondLineHeadDelay: Int = 1000
    public const val SecondLineTailDelay: Int = 1267

    public val FirstLineHeadEasing: CubicBezierEasing = CubicBezierEasing(0.2f, 0f, 0.8f, 1f)
    public val FirstLineTailEasing: CubicBezierEasing = CubicBezierEasing(0.4f, 0f, 1f, 1f)
    public val SecondLineHeadEasing: CubicBezierEasing = CubicBezierEasing(0f, 0f, 0.65f, 1f)
    public val SecondLineTailEasing: CubicBezierEasing = CubicBezierEasing(0.1f, 0f, 0.45f, 1f)
}

@Composable
@Preview
internal fun LinearProgressIndicatorPreview() {
    AppPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp),
        ) {
            BasicText(
                text = "Determinate Progress",
                style = AppTheme.typography.body1,
            )
            LinearProgressIndicator(progress = { 0.7f })

            BasicText(
                text = "Indeterminate Progress",
                style = AppTheme.typography.body1,
            )
            LinearProgressIndicator()
        }
    }
}
