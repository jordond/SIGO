package now.shouldigooutside.core.ui.components.progressindicators

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.progress
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalContainerColor
import now.shouldigooutside.core.ui.components.BrutalContainer
import now.shouldigooutside.core.ui.components.BrutalDefaults
import now.shouldigooutside.core.ui.components.BrutalElevationDefaults
import now.shouldigooutside.core.ui.components.Surface
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.contentColorFor
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import kotlin.math.abs

@Composable
public fun LinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    height: Dp = LinearProgressIndicatorDefaults.TrackHeight,
    color: Color = LinearProgressIndicatorDefaults.Color,
    trackColor: Color = LinearProgressIndicatorDefaults.TrackColor,
    strokeCap: StrokeCap = LinearProgressIndicatorDefaults.StrokeStyle,
    shape: Shape = LinearProgressIndicatorDefaults.Shape,
    elevation: Dp = LinearProgressIndicatorDefaults.Elevation,
    showEndMarker: Boolean = LinearProgressIndicatorDefaults.ShowEndMarker,
    textPosition: ProgressTextPosition = LinearProgressIndicatorDefaults.TextPosition,
    progressTextStyle: TextStyle = LinearProgressIndicatorDefaults.ProgressTextStyle.copy(
        color = contentColorFor(color),
    ),
) {
    val borderColor = LinearProgressIndicatorDefaults.BorderColor
    val border = LinearProgressIndicatorDefaults.borderStroke()
    val coercedProgress = { progress().coerceIn(0f, 1f) }
    val currentProgress = coercedProgress()
    var progressPositionOffset by remember { mutableStateOf(0f) }

    val progressTextAlignment: Alignment? = remember(textPosition) {
        when (textPosition) {
            ProgressTextPosition.Start -> Alignment.CenterStart
            ProgressTextPosition.Center -> Alignment.Center
            ProgressTextPosition.End -> Alignment.CenterEnd
            ProgressTextPosition.Follow,
            ProgressTextPosition.None,
            -> null
        }
    }

    val percentValue = remember(currentProgress) { (currentProgress * 100).toInt() }

    BrutalContainer(
        shape = shape,
        elevation = elevation,
        color = borderColor,
        modifier = modifier.semantics(mergeDescendants = true) {
            progressBarRangeInfo = ProgressBarRangeInfo(currentProgress, 0f..1f)
        },
    ) {
        Surface(
            shape = shape,
            border = border,
            color = trackColor,
            shadowElevation = 0.dp,
        ) {
            Box {
                Canvas(
                    modifier = Modifier
                        .height(height)
                        .fillMaxWidth(),
                ) {
                    val strokeWidth = size.height
                    drawLinearIndicatorTrack(trackColor, strokeWidth, strokeCap)

                    val progressEndPosition = getProgressEndPosition(
                        progress = currentProgress,
                        width = size.width,
                        strokeWidth = strokeWidth,
                        strokeCap = strokeCap,
                    )

                    if (textPosition == ProgressTextPosition.Follow) {
                        progressPositionOffset = progressEndPosition - (strokeWidth * 1.1f)
                    }

                    drawLinearIndicator(
                        startFraction = 0f,
                        endFraction = currentProgress,
                        color = color,
                        strokeWidth = strokeWidth,
                        strokeCap = strokeCap,
                        isDeterminant = true,
                    )
                    if (showEndMarker && coercedProgress() > 0f && coercedProgress() < 1f) {
                        drawProgressEndMarker(
                            markerPosition = progressEndPosition,
                            color = borderColor,
                            markerWidth = border.width.toPx(),
                            strokeCap = strokeCap,
                        )
                    }
                }

                if (textPosition != ProgressTextPosition.None) {
                    val textWidth = remember(height) { height * 1.3f }
                    val percentText = Res.string.progress.get(percentValue)
                    val percentStyle = remember(progressTextStyle) {
                        progressTextStyle.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(height)
                            .let { currentModifier ->
                                if (progressTextAlignment == null) {
                                    currentModifier
                                } else {
                                    currentModifier.then(Modifier.align(progressTextAlignment))
                                }
                            }.graphicsLayer {
                                if (textPosition == ProgressTextPosition.Follow) {
                                    translationX = progressPositionOffset - (height * 0.3f).toPx()
                                }
                            },
                    ) {
                        BasicText(
                            text = percentText,
                            style = percentStyle,
                            maxLines = 1,
                            autoSize = TextAutoSize.StepBased(minFontSize = 10.sp),
                            modifier = Modifier
                                .width(textWidth)
                                .padding(horizontal = 8.dp)
                                .align(Alignment.Center),
                        )
                    }
                }
            }
        }
    }
}

@Composable
public fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    height: Dp = LinearProgressIndicatorDefaults.TrackHeight,
    color: Color = LinearProgressIndicatorDefaults.Color,
    trackColor: Color = LinearProgressIndicatorDefaults.TrackColor,
    strokeCap: StrokeCap = LinearProgressIndicatorDefaults.StrokeStyle,
    shape: Shape = LinearProgressIndicatorDefaults.Shape,
    elevation: Dp = LinearProgressIndicatorDefaults.Elevation,
    showEndMarker: Boolean = LinearProgressIndicatorDefaults.ShowEndMarker,
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

    val borderColor = LinearProgressIndicatorDefaults.BorderColor
    val border = LinearProgressIndicatorDefaults.borderStroke()

    BrutalContainer(
        shape = shape,
        elevation = elevation,
        color = borderColor,
        modifier = modifier.progressSemantics(),
    ) {
        Surface(
            shape = shape,
            border = border,
            color = trackColor,
            shadowElevation = 0.dp,
        ) {
            Canvas(
                modifier = Modifier
                    .height(height)
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
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
    isDeterminant: Boolean = false,
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
        val adjustedBarStart =
            if (isDeterminant && strokeCap == StrokeCap.Round) {
                0f
            } else {
                barStart.coerceIn(coerceRange)
            }
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

private fun DrawScope.drawProgressEndMarker(
    markerPosition: Float,
    color: Color,
    markerWidth: Float,
    strokeCap: StrokeCap,
) {
    val height = size.height

    when (strokeCap) {
        StrokeCap.Round -> {
            val radius = height / 2
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(markerPosition - (radius * 2), 0f),
                size = Size(radius * 2, height),
                style = Stroke(markerWidth),
            )
        }
        else -> {
            drawLine(
                color = color,
                start = Offset(markerPosition, 0f),
                end = Offset(markerPosition, height),
                strokeWidth = markerWidth,
            )
        }
    }
}

private fun getProgressEndPosition(
    progress: Float,
    width: Float,
    strokeWidth: Float,
    strokeCap: StrokeCap,
): Float {
    val isLtr = true // Always LTR for calculation purposes
    val endFraction = if (isLtr) progress else 1f - progress
    val barEnd = endFraction * width

    // For StrokeCap.Butt, the end position is exactly at the mathematical endpoint
    // For StrokeCap.Round and StrokeCap.Square, we need to account for the cap extension
    return when {
        strokeCap == StrokeCap.Butt || strokeWidth > width -> {
            barEnd
        }
        else -> {
            val strokeCapOffset = strokeWidth / 2
            // Apply the coerced range to ensure the endpoint stays within bounds
            val adjustedBarEnd = barEnd.coerceIn(strokeCapOffset..(width - strokeCapOffset))

            // For non-Butt stroke caps, adjust the marker position based on cap type
            when (strokeCap) {
                StrokeCap.Round, StrokeCap.Square -> {
                    // For Round and Square caps, the visual end extends by half strokeWidth
                    if (progress < 1f) adjustedBarEnd + strokeCapOffset else width
                }
                else -> {
                    adjustedBarEnd
                } // Should never reach here
            }
        }
    }
}

public enum class ProgressTextPosition {
    Start,
    Center,
    End,
    Follow,
    None,
}

@Suppress("ConstPropertyName")
public object LinearProgressIndicatorDefaults {
    public const val ShowEndMarker: Boolean = true
    public val BorderColor: Color @Composable get() = BrutalDefaults.Color
    public val BorderWidth: Dp = BrutalDefaults.BorderWidth
    public val Shape: Shape @Composable get() = AppTheme.shapes.extraSmall
    public val Elevation: Dp = BrutalElevationDefaults.Small.default
    public val TextPosition: ProgressTextPosition = ProgressTextPosition.Follow
    public val ProgressTextStyle: TextStyle @Composable get() = AppTheme.typography.label3

    public val Color: Color
        @Composable get() = AppTheme.colors.primary

    public val TrackColor: Color
        @Composable get() = LocalContainerColor.current

    public val TrackHeight: Dp = 32.dp
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

    @Composable
    internal fun borderStroke(
        color: Color = BorderColor,
        width: Dp = BorderWidth,
    ): BorderStroke = BorderStroke(width, color)
}

@Composable
private fun IndicatorPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = "Determinate Progress",
            style = AppTheme.typography.h4,
        )
        LinearProgressIndicator(
            progress = { 0.7f },
            showEndMarker = false,
            textPosition = ProgressTextPosition.Start,
        )
        LinearProgressIndicator(
            progress = { 0.7f },
            showEndMarker = false,
            textPosition = ProgressTextPosition.Center,
        )
        LinearProgressIndicator(
            progress = { 0.7f },
            showEndMarker = false,
            textPosition = ProgressTextPosition.End,
        )

        Text(
            text = "Determinate Progress with End Marker",
            style = AppTheme.typography.h4,
        )

        var progress by remember { mutableStateOf(0.4f) }
        val animatedProgress by animateFloatAsState(progress)
        LaunchedEffect(Unit) {
            while (isActive) {
                progress = (progress + 0.01f).coerceAtMost(1f)
                if (progress == 1f) {
                    progress = 0f
                }
                delay(100)
            }
        }

        LinearProgressIndicator(
            strokeCap = StrokeCap.Square,
            showEndMarker = true,
            progress = { animatedProgress },
        )
        LinearProgressIndicator(
            strokeCap = StrokeCap.Round,
            showEndMarker = true,
            progress = { animatedProgress },
        )
        LinearProgressIndicator(
            strokeCap = StrokeCap.Butt,
            showEndMarker = true,
            progress = { animatedProgress },
        )

        Text(
            text = "Indeterminate Progress",
            style = AppTheme.typography.h4,
        )
        LinearProgressIndicator()
    }
}

@Composable
@Preview
internal fun LinearProgressIndicatorLightPreview() {
    AppPreview(isDarkTheme = false) {
        IndicatorPreview()
    }
}

@Composable
@Preview
internal fun LinearProgressIndicatorDarkPreview() {
    AppPreview(isDarkTheme = true) {
        IndicatorPreview()
    }
}
