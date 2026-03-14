package app.sigot.core.ui.foundation.slider

import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.lerp
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import app.sigot.core.ui.foundation.IncreaseHorizontalSemanticsBounds
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.SliderRangeTolerance
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.ThumbHeight
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.ThumbShape
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.ThumbSizeOnPress
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.ThumbTrackGap
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.ThumbWidth
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.TrackHeight
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.TrackInsideCornerSize
import app.sigot.core.ui.foundation.slider.SliderFoundationDefaults.TrackTickSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
internal fun BasicSlider(
    state: SliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SliderColors,
    trackHeight: Dp = TrackHeight,
    thumbWidth: Dp = ThumbWidth,
    thumbHeight: Dp = ThumbHeight,
    thumbSizeOnPress: DpSize = DpSize(thumbWidth, thumbHeight),
    trackInsideCornerSize: Dp = TrackInsideCornerSize,
    thumbTrackGap: Dp = ThumbTrackGap,
    trackTickSize: Dp = TrackTickSize,
    thumbShape: Shape = ThumbShape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderState) -> Unit = {
        SliderFoundationDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
            thumbShape = thumbShape,
            thumbSizeOnPress = thumbSizeOnPress,
            thumbWidth = thumbWidth,
            thumbHeight = thumbHeight,
        )
    },
    track: @Composable (SliderState) -> Unit = { sliderState ->
        SliderFoundationDefaults.Track(
            colors = colors,
            enabled = enabled,
            sliderState = sliderState,
            trackHeight = trackHeight,
            thumbTrackGap = thumbTrackGap,
            trackTickSize = trackTickSize,
            trackInsideCornerSize = trackInsideCornerSize,
        )
    },
) {
    require(state.steps >= 0) { "steps should be >= 0" }

    SliderComponent(
        state = state,
        modifier = modifier,
        enabled = enabled,
        thumbWidth = thumbWidth,
        trackHeight = trackHeight,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track,
    )
}

@Composable
internal fun BasicRangeSlider(
    state: RangeSliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SliderColors,
    trackHeight: Dp = TrackHeight,
    thumbWidth: Dp = ThumbWidth,
    thumbHeight: Dp = ThumbHeight,
    thumbSizeOnPress: DpSize = ThumbSizeOnPress,
    trackInsideCornerSize: Dp = TrackInsideCornerSize,
    thumbTrackGap: Dp = ThumbTrackGap,
    trackTickSize: Dp = TrackTickSize,
    thumbShape: Shape = ThumbShape,
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    startThumb: @Composable (RangeSliderState) -> Unit = {
        SliderFoundationDefaults.Thumb(
            interactionSource = startInteractionSource,
            colors = colors,
            enabled = enabled,
            thumbShape = thumbShape,
            thumbSizeOnPress = thumbSizeOnPress,
            thumbWidth = thumbWidth,
            thumbHeight = thumbHeight,
        )
    },
    endThumb: @Composable (RangeSliderState) -> Unit = {
        SliderFoundationDefaults.Thumb(
            interactionSource = endInteractionSource,
            colors = colors,
            enabled = enabled,
            thumbShape = thumbShape,
            thumbSizeOnPress = thumbSizeOnPress,
            thumbWidth = thumbWidth,
            thumbHeight = thumbHeight,
        )
    },
    track: @Composable (RangeSliderState) -> Unit = { rangeSliderState ->
        SliderFoundationDefaults.Track(
            colors = colors,
            enabled = enabled,
            rangeSliderState = rangeSliderState,
            trackHeight = trackHeight,
            thumbTrackGap = thumbTrackGap,
            trackTickSize = trackTickSize,
            trackInsideCornerSize = trackInsideCornerSize,
        )
    },
) {
    require(state.steps >= 0) { "steps should be >= 0" }

    RangeSliderComponent(
        modifier = modifier,
        state = state,
        enabled = enabled,
        thumbWidth = thumbWidth,
        trackHeight = trackHeight,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        startThumb = startThumb,
        endThumb = endThumb,
        track = track,
    )
}

@Composable
private fun SliderComponent(
    modifier: Modifier,
    state: SliderState,
    thumbWidth: Dp,
    trackHeight: Dp,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    thumb: @Composable (SliderState) -> Unit,
    track: @Composable (SliderState) -> Unit,
) {
    state.isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val press = Modifier.sliderTapModifier(state, interactionSource, enabled)
    val drag = Modifier.draggable(
        orientation = Orientation.Horizontal,
        reverseDirection = state.isRtl,
        enabled = enabled,
        interactionSource = interactionSource,
        onDragStopped = { state.gestureEndAction() },
        startDragImmediately = state.isDragging,
        state = state,
    )

    Layout(
        content = {
            Box(
                modifier = Modifier
                    .layoutId(SliderComponents.THUMB)
                    .wrapContentWidth()
                    .onSizeChanged { size ->
                        state.thumbWidth = size.width.toFloat()
                    },
            ) {
                thumb(state)
            }
            Box(modifier = Modifier.layoutId(SliderComponents.TRACK)) { track(state) }
        },
        modifier = modifier
            .minimumInteractiveComponentSize()
            .requiredSizeIn(minWidth = thumbWidth, minHeight = trackHeight)
            .sliderSemantics(state, enabled)
            .focusable(enabled, interactionSource)
            .slideOnKeyEvents(
                enabled = enabled,
                steps = state.steps,
                valueRange = state.valueRange,
                value = state.value,
                isRtl = state.isRtl,
                onValueChangeState = state.onValueChange,
                onValueChangeFinishedState = state.onValueChangeFinished,
            ).then(press)
            .then(drag),
    ) { measurables, constraints ->
        val thumbPlaceable =
            measurables.fastFirst { it.layoutId == SliderComponents.THUMB }.measure(constraints)

        val trackPlaceable = measurables
            .fastFirst { it.layoutId == SliderComponents.TRACK }
            .measure(constraints.offset(horizontal = -thumbPlaceable.width).copy(minHeight = 0))

        val sliderWidth = thumbPlaceable.width + trackPlaceable.width
        val sliderHeight = max(trackPlaceable.height, thumbPlaceable.height)

        state.updateDimensions(trackPlaceable.height.toFloat(), sliderWidth)

        val trackOffsetX = thumbPlaceable.width / 2
        val thumbOffsetX = (trackPlaceable.width * state.coercedValueAsFraction).roundToInt()
        val trackOffsetY = (sliderHeight - trackPlaceable.height) / 2
        val thumbOffsetY =
            ((sliderHeight - thumbPlaceable.height) / 2)

        layout(sliderWidth, sliderHeight) {
            trackPlaceable.placeRelative(trackOffsetX, trackOffsetY)
            thumbPlaceable.placeRelative(thumbOffsetX, thumbOffsetY)
        }
    }
}

@Composable
private fun RangeSliderComponent(
    modifier: Modifier,
    state: RangeSliderState,
    enabled: Boolean,
    thumbWidth: Dp,
    trackHeight: Dp,
    startInteractionSource: MutableInteractionSource,
    endInteractionSource: MutableInteractionSource,
    startThumb: @Composable ((RangeSliderState) -> Unit),
    endThumb: @Composable ((RangeSliderState) -> Unit),
    track: @Composable ((RangeSliderState) -> Unit),
) {
    state.isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    val pressDrag = Modifier.rangeSliderPressDragModifier(
        state,
        startInteractionSource,
        endInteractionSource,
        enabled,
    )

    Layout(
        {
            Box(
                modifier = Modifier
                    .layoutId(RangeSliderComponents.STARTTHUMB)
                    .wrapContentWidth()
                    .onSizeChanged { state.startThumbWidth = it.width.toFloat() }
                    .rangeSliderStartThumbSemantics(state, enabled)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Slider Range Start"
                    }.focusable(enabled, startInteractionSource),
            ) {
                startThumb(state)
            }
            Box(
                modifier = Modifier
                    .layoutId(RangeSliderComponents.ENDTHUMB)
                    .wrapContentWidth()
                    .onSizeChanged { state.endThumbWidth = it.width.toFloat() }
                    .rangeSliderEndThumbSemantics(state, enabled)
                    .semantics(mergeDescendants = true) {
                        contentDescription = "Slider Range End"
                    }.focusable(enabled, endInteractionSource),
            ) {
                endThumb(state)
            }
            Box(modifier = Modifier.layoutId(RangeSliderComponents.TRACK)) { track(state) }
        },
        modifier = modifier
            .minimumInteractiveComponentSize()
            .requiredSizeIn(minWidth = thumbWidth, minHeight = trackHeight)
            .then(pressDrag),
    ) { measurables, constraints ->
        val startThumbPlaceable =
            measurables.fastFirst { it.layoutId == RangeSliderComponents.STARTTHUMB }.measure(constraints)

        val endThumbPlaceable =
            measurables.fastFirst { it.layoutId == RangeSliderComponents.ENDTHUMB }.measure(constraints)

        val trackPlaceable = measurables.fastFirst { it.layoutId == RangeSliderComponents.TRACK }.measure(
            constraints
                .offset(
                    horizontal = -(startThumbPlaceable.width + endThumbPlaceable.width) / 2,
                ).copy(minHeight = 0),
        )

        val sliderWidth = trackPlaceable.width + (startThumbPlaceable.width + endThumbPlaceable.width) / 2
        val sliderHeight = maxOf(trackPlaceable.height, startThumbPlaceable.height, endThumbPlaceable.height)

        state.trackHeight = trackPlaceable.height.toFloat()
        state.totalWidth = sliderWidth

        state.updateMinMaxPx()

        val trackOffsetX = startThumbPlaceable.width / 2
        val startThumbOffsetX = (trackPlaceable.width * state.coercedActiveRangeStartAsFraction).roundToInt()
        // When start thumb and end thumb have different widths,
        // we need to add a correction for the centering of the slider.
        val endCorrection = (startThumbPlaceable.width - endThumbPlaceable.width) / 2
        val endThumbOffsetX =
            (trackPlaceable.width * state.coercedActiveRangeEndAsFraction + endCorrection).roundToInt()
        val trackOffsetY = (sliderHeight - trackPlaceable.height) / 2
        val startThumbOffsetY = (sliderHeight - startThumbPlaceable.height) / 2
        val endThumbOffsetY = (sliderHeight - endThumbPlaceable.height) / 2

        layout(sliderWidth, sliderHeight) {
            trackPlaceable.placeRelative(trackOffsetX, trackOffsetY)
            startThumbPlaceable.placeRelative(startThumbOffsetX, startThumbOffsetY)
            endThumbPlaceable.placeRelative(endThumbOffsetX, endThumbOffsetY)
        }
    }
}

private fun Modifier.rangeSliderStartThumbSemantics(
    state: RangeSliderState,
    enabled: Boolean,
): Modifier {
    val valueRange = state.valueRange.start..state.activeRangeEnd

    return semantics {
        if (!enabled) disabled()
        setProgress(
            action = { targetValue ->
                var newValue = targetValue.coerceIn(valueRange.start, valueRange.endInclusive)
                val originalVal = newValue
                val resolvedValue = if (state.startSteps > 0) {
                    var distance: Float = newValue
                    for (i in 0..state.startSteps + 1) {
                        val stepValue = lerp(
                            valueRange.start,
                            valueRange.endInclusive,
                            i.toFloat() / (state.startSteps + 1),
                        )
                        if (abs(stepValue - originalVal) <= distance) {
                            distance = abs(stepValue - originalVal)
                            newValue = stepValue
                        }
                    }
                    newValue
                } else {
                    newValue
                }

                // This is to keep it consistent with AbsSeekbar.java: return false if no
                // change from current.
                if (resolvedValue == state.activeRangeStart) {
                    false
                } else {
                    val resolvedRange = SliderRange(resolvedValue, state.activeRangeEnd)
                    val activeRange = SliderRange(state.activeRangeStart, state.activeRangeEnd)
                    if (resolvedRange != activeRange) {
                        if (state.onValueChange != null) {
                            state.onValueChange?.let { it(resolvedRange) }
                        } else {
                            state.activeRangeStart = resolvedRange.start
                            state.activeRangeEnd = resolvedRange.endInclusive
                        }
                    }
                    state.onValueChangeFinished?.invoke()
                    true
                }
            },
        )
    }.then(IncreaseHorizontalSemanticsBounds)
        .progressSemantics(state.activeRangeStart, valueRange, state.startSteps)
}

private fun Modifier.slideOnKeyEvents(
    enabled: Boolean,
    steps: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    value: Float,
    isRtl: Boolean,
    onValueChangeState: ((Float) -> Unit)?,
    onValueChangeFinishedState: (() -> Unit)?,
): Modifier {
    require(steps >= 0) { "steps should be >= 0" }
    return this.onKeyEvent { event ->
        if (!enabled) return@onKeyEvent false
        if (onValueChangeState == null) return@onKeyEvent false
        when (event.type) {
            KeyEventType.KeyDown -> {
                val rangeLength = abs(valueRange.endInclusive - valueRange.start)
                // When steps == 0, it means that a user is not limited by a step length (delta)
                // when using touch or mouse. But it is not possible to adjust the value
                // continuously when using keyboard buttons - the delta has to be discrete.
                // In this case, 1% of the valueRange seems to make sense.
                val actualSteps = if (steps > 0) steps + 1 else 100
                val delta = rangeLength / actualSteps
                when (event.key) {
                    Key.DirectionUp -> {
                        onValueChangeState((value + delta).coerceIn(valueRange))
                        true
                    }
                    Key.DirectionDown -> {
                        onValueChangeState((value - delta).coerceIn(valueRange))
                        true
                    }
                    Key.DirectionRight -> {
                        val sign = if (isRtl) -1 else 1
                        onValueChangeState((value + sign * delta).coerceIn(valueRange))
                        true
                    }
                    Key.DirectionLeft -> {
                        val sign = if (isRtl) -1 else 1
                        onValueChangeState((value - sign * delta).coerceIn(valueRange))
                        true
                    }
                    Key.MoveHome -> {
                        onValueChangeState(valueRange.start)
                        true
                    }
                    Key.MoveEnd -> {
                        onValueChangeState(valueRange.endInclusive)
                        true
                    }
                    Key.PageUp -> {
                        val page = (actualSteps / 10).coerceIn(1, 10)
                        onValueChangeState((value - page * delta).coerceIn(valueRange))
                        true
                    }
                    Key.PageDown -> {
                        val page = (actualSteps / 10).coerceIn(1, 10)
                        onValueChangeState((value + page * delta).coerceIn(valueRange))
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            KeyEventType.KeyUp -> {
                when (event.key) {
                    Key.DirectionUp,
                    Key.DirectionDown,
                    Key.DirectionRight,
                    Key.DirectionLeft,
                    Key.MoveHome,
                    Key.MoveEnd,
                    Key.PageUp,
                    Key.PageDown,
                    -> {
                        onValueChangeFinishedState?.invoke()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            else -> {
                false
            }
        }
    }
}

@Suppress("ConstPropertyName")
internal object SliderFoundationDefaults {
    internal val TrackHeight = 32.0.dp
    internal val ThumbWidth = TrackHeight
    internal val ThumbHeight = TrackHeight
    internal val ThumbSize = DpSize(ThumbWidth, ThumbHeight)
    internal val ThumbSizeOnPress = ThumbSize
    internal val ThumbTrackGap: Dp = 0.dp
    internal val TrackInsideCornerSize: Dp = 2.dp
    internal val TrackTickSize = 4.dp
    internal val ThumbShape = RoundedCornerShape(50)
    internal const val SliderRangeTolerance = 0.0001

    @Composable
    fun sliderStyle() = SliderStyle()

    @Composable
    fun Thumb(
        interactionSource: MutableInteractionSource,
        modifier: Modifier = Modifier,
        colors: SliderColors,
        enabled: Boolean = true,
        thumbWidth: Dp = ThumbWidth,
        thumbHeight: Dp = ThumbHeight,
        thumbSizeOnPress: DpSize = ThumbSizeOnPress,
        thumbShape: Shape = ThumbShape,
    ) {
        val thumbSize = remember {
            mutableStateOf(DpSize(thumbWidth, thumbHeight))
        }
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        val size = if (interactions.isNotEmpty()) {
            thumbSizeOnPress
        } else {
            thumbSize.value
        }

        val thumbColor = colors.thumbColor(enabled)

        val thumbInteractionModifier = modifier
            .size(size)
            .hoverable(interactionSource = interactionSource)
            .background(thumbColor, thumbShape)

        Spacer(modifier = thumbInteractionModifier)
    }

    @Composable
    fun Track(
        sliderState: SliderState,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        colors: SliderColors,
        trackTickSize: Dp = TrackTickSize,
        drawTick: DrawScope.(Offset, Color) -> Unit = { offset, color ->
            drawStopIndicator(drawScope = this, offset = offset, color = color, size = trackTickSize)
        },
        thumbTrackGap: Dp = ThumbTrackGap,
        trackInsideCornerSize: Dp = TrackInsideCornerSize,
        trackHeight: Dp = TrackHeight,
    ) {
        val inactiveTrackColor = colors.trackColor(enabled, active = false)
        val activeTrackColor = colors.trackColor(enabled, active = true)
        val inactiveTickColor = colors.tickColor(enabled, active = false)
        val activeTickColor = colors.tickColor(enabled, active = true)
        Canvas(
            modifier
                .fillMaxWidth()
                .height(trackHeight)
                .rotate(if (LocalLayoutDirection.current == LayoutDirection.Rtl) 180f else 0f),
        ) {
            drawTrack(
                tickFractions = sliderState.tickFractions,
                activeRangeStart = 0f,
                activeRangeEnd = sliderState.coercedValueAsFraction,
                inactiveTrackColor = inactiveTrackColor,
                activeTrackColor = activeTrackColor,
                inactiveTickColor = inactiveTickColor,
                activeTickColor = activeTickColor,
                height = sliderState.trackHeight.toDp(),
                startThumbWidth = 0.toDp(),
                endThumbWidth = sliderState.thumbWidth.toDp(),
                thumbTrackGap = thumbTrackGap,
                trackInsideCornerSize = trackInsideCornerSize,
                drawTick = drawTick,
                isRangeSlider = false,
            )
        }
    }

    // Track for RangeSlider
    @Composable
    fun Track(
        rangeSliderState: RangeSliderState,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        colors: SliderColors,
        trackTickSize: Dp = TrackTickSize,
        drawTick: DrawScope.(Offset, Color) -> Unit = { offset, color ->
            drawStopIndicator(drawScope = this, offset = offset, color = color, size = trackTickSize)
        },
        thumbTrackGap: Dp = ThumbTrackGap,
        trackInsideCornerSize: Dp = TrackInsideCornerSize,
        trackHeight: Dp = TrackHeight,
    ) {
        val inactiveTrackColor = colors.trackColor(enabled, active = false)
        val activeTrackColor = colors.trackColor(enabled, active = true)
        val inactiveTickColor = colors.tickColor(enabled, active = false)
        val activeTickColor = colors.tickColor(enabled, active = true)
        Canvas(
            modifier
                .fillMaxWidth()
                .height(trackHeight)
                .rotate(if (LocalLayoutDirection.current == LayoutDirection.Rtl) 180f else 0f),
        ) {
            drawTrack(
                tickFractions = rangeSliderState.tickFractions,
                activeRangeStart = rangeSliderState.coercedActiveRangeStartAsFraction,
                activeRangeEnd = rangeSliderState.coercedActiveRangeEndAsFraction,
                inactiveTrackColor = inactiveTrackColor,
                activeTrackColor = activeTrackColor,
                inactiveTickColor = inactiveTickColor,
                activeTickColor = activeTickColor,
                height = rangeSliderState.trackHeight.toDp(),
                startThumbWidth = rangeSliderState.startThumbWidth.toDp(),
                endThumbWidth = rangeSliderState.endThumbWidth.toDp(),
                thumbTrackGap = thumbTrackGap,
                trackInsideCornerSize = trackInsideCornerSize,
                drawTick = drawTick,
                isRangeSlider = true,
            )
        }
    }

    private fun DrawScope.drawTrack(
        tickFractions: FloatArray,
        activeRangeStart: Float,
        activeRangeEnd: Float,
        inactiveTrackColor: Color,
        activeTrackColor: Color,
        inactiveTickColor: Color,
        activeTickColor: Color,
        height: Dp,
        startThumbWidth: Dp,
        endThumbWidth: Dp,
        thumbTrackGap: Dp,
        trackInsideCornerSize: Dp,
        drawTick: DrawScope.(Offset, Color) -> Unit,
        isRangeSlider: Boolean,
    ) {
        val sliderStart = Offset(0f, center.y)
        val sliderEnd = Offset(size.width, center.y)
        val trackStrokeWidth = height.toPx()

        val sliderValueEnd = Offset(sliderStart.x + (sliderEnd.x - sliderStart.x) * activeRangeEnd, center.y)

        val sliderValueStart =
            Offset(sliderStart.x + (sliderEnd.x - sliderStart.x) * activeRangeStart, center.y)

        val cornerSize = trackStrokeWidth / 2
        val insideCornerSize = trackInsideCornerSize.toPx()
        var startGap = 0f
        var endGap = 0f
        if (thumbTrackGap > 0.dp) {
            startGap = startThumbWidth.toPx() / 2 + thumbTrackGap.toPx()
            endGap = endThumbWidth.toPx() / 2 + thumbTrackGap.toPx()
        }

        // inactive track (range slider)
        if (isRangeSlider && sliderValueStart.x > sliderStart.x + startGap + cornerSize) {
            val start = sliderStart.x
            val end = sliderValueStart.x - startGap
            drawTrackPath(
                Offset.Zero,
                Size(end - start, trackStrokeWidth),
                inactiveTrackColor,
                cornerSize,
                insideCornerSize,
            )
        }
        // inactive track
        if (sliderValueEnd.x < sliderEnd.x - endGap - cornerSize) {
            val start = sliderValueEnd.x + endGap
            val end = sliderEnd.x
            drawTrackPath(
                Offset(start, 0f),
                Size(end - start, trackStrokeWidth),
                inactiveTrackColor,
                insideCornerSize,
                cornerSize,
            )
        }
        // active track
        val activeTrackStart = if (isRangeSlider) sliderValueStart.x + startGap else 0f
        val activeTrackEnd = sliderValueEnd.x - endGap
        val startCornerRadius = if (isRangeSlider) insideCornerSize else cornerSize
        if (activeTrackEnd - activeTrackStart > startCornerRadius) {
            drawTrackPath(
                Offset(activeTrackStart, 0f),
                Size(activeTrackEnd - activeTrackStart, trackStrokeWidth),
                activeTrackColor,
                startCornerRadius,
                insideCornerSize,
            )
        }

        val start = Offset(sliderStart.x + cornerSize, sliderStart.y)
        val end = Offset(sliderEnd.x - cornerSize, sliderEnd.y)
        val tickStartGap = sliderValueStart.x - startGap..sliderValueStart.x + startGap
        val tickEndGap = sliderValueEnd.x - endGap..sliderValueEnd.x + endGap
        tickFractions.forEachIndexed { _, tick ->
            val outsideFraction = tick > activeRangeEnd || tick < activeRangeStart
            val center = Offset(lerp(start, end, tick).x, center.y)
            // skip ticks that fall on a gap
            if ((isRangeSlider && center.x in tickStartGap) || center.x in tickEndGap) {
                return@forEachIndexed
            }
            drawTick(
                this,
                center, // offset
                if (outsideFraction) inactiveTickColor else activeTickColor, // color
            )
        }
    }

    private fun DrawScope.drawTrackPath(
        offset: Offset,
        size: Size,
        color: Color,
        startCornerRadius: Float,
        endCornerRadius: Float,
    ) {
        val startCorner = CornerRadius(startCornerRadius, startCornerRadius)
        val endCorner = CornerRadius(endCornerRadius, endCornerRadius)
        val track = RoundRect(
            rect = Rect(Offset(offset.x, 0f), size = Size(size.width, size.height)),
            topLeft = startCorner,
            topRight = endCorner,
            bottomRight = endCorner,
            bottomLeft = startCorner,
        )
        trackPath.addRoundRect(track)
        drawPath(trackPath, color)
        trackPath.rewind()
    }

    private fun drawStopIndicator(
        drawScope: DrawScope,
        offset: Offset,
        size: Dp,
        color: Color,
    ) {
        with(drawScope) { drawCircle(color = color, center = offset, radius = size.toPx() / 2f) }
    }

    private val trackPath = Path()
}

private fun Modifier.rangeSliderEndThumbSemantics(
    state: RangeSliderState,
    enabled: Boolean,
): Modifier {
    val valueRange = state.activeRangeStart..state.valueRange.endInclusive

    return semantics {
        if (!enabled) disabled()

        setProgress(
            action = { targetValue ->
                var newValue = targetValue.coerceIn(valueRange.start, valueRange.endInclusive)
                val originalVal = newValue
                val resolvedValue = if (state.endSteps > 0) {
                    var distance: Float = newValue
                    for (i in 0..state.endSteps + 1) {
                        val stepValue = lerp(
                            valueRange.start,
                            valueRange.endInclusive,
                            i.toFloat() / (state.endSteps + 1),
                        )
                        if (abs(stepValue - originalVal) <= distance) {
                            distance = abs(stepValue - originalVal)
                            newValue = stepValue
                        }
                    }
                    newValue
                } else {
                    newValue
                }

                // This is to keep it consistent with AbsSeekbar.java: return false if no
                // change from current.
                if (resolvedValue == state.activeRangeEnd) {
                    false
                } else {
                    val resolvedRange = SliderRange(state.activeRangeStart, resolvedValue)
                    val activeRange = SliderRange(state.activeRangeStart, state.activeRangeEnd)
                    if (resolvedRange != activeRange) {
                        if (state.onValueChange != null) {
                            state.onValueChange?.let { it(resolvedRange) }
                        } else {
                            state.activeRangeStart = resolvedRange.start
                            state.activeRangeEnd = resolvedRange.endInclusive
                        }
                    }
                    state.onValueChangeFinished?.invoke()
                    true
                }
            },
        )
    }.then(IncreaseHorizontalSemanticsBounds)
        .progressSemantics(state.activeRangeEnd, valueRange, state.endSteps)
}

@Stable
private fun Modifier.sliderTapModifier(
    state: SliderState,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
) = if (!enabled) {
    this
} else {
    pointerInput(state, interactionSource) {
        detectTapGestures(
            onPress = { state.onPress(it) },
            onTap = {
                state.dispatchRawDelta(0f)
                state.gestureEndAction()
            },
        )
    }
}

@Stable
private fun Modifier.rangeSliderPressDragModifier(
    state: RangeSliderState,
    startInteractionSource: MutableInteractionSource,
    endInteractionSource: MutableInteractionSource,
    enabled: Boolean,
): Modifier =
    if (enabled) {
        pointerInput(startInteractionSource, endInteractionSource, state) {
            val rangeSliderLogic = RangeSliderLogic(state, startInteractionSource, endInteractionSource)
            coroutineScope {
                awaitEachGesture {
                    val event = awaitFirstDown(requireUnconsumed = false)
                    val interaction = DragInteraction.Start()
                    var posX = if (state.isRtl) state.totalWidth - event.position.x else event.position.x
                    val compare = rangeSliderLogic.compareOffsets(posX)
                    var draggingStart = if (compare != 0) {
                        compare < 0
                    } else {
                        state.rawOffsetStart > posX
                    }

                    awaitSlop(event.id, event.type)?.let {
                        val slop = viewConfiguration.pointerSlop(event.type)
                        val shouldUpdateCapturedThumb =
                            abs(state.rawOffsetEnd - posX) < slop && abs(state.rawOffsetStart - posX) < slop
                        if (shouldUpdateCapturedThumb) {
                            val dir = it.second
                            draggingStart = if (state.isRtl) dir >= 0f else dir < 0f
                            posX += it.first.positionChange().x
                        }
                    }

                    rangeSliderLogic.captureThumb(
                        draggingStart,
                        posX,
                        interaction,
                        this@coroutineScope,
                    )

                    val finishInteraction = try {
                        val success = horizontalDrag(pointerId = event.id) {
                            val deltaX = it.positionChange().x
                            state.onDrag(
                                draggingStart,
                                if (state.isRtl) -deltaX else deltaX,
                            )
                        }
                        if (success) {
                            DragInteraction.Stop(interaction)
                        } else {
                            DragInteraction.Cancel(interaction)
                        }
                    } catch (_: CancellationException) {
                        DragInteraction.Cancel(interaction)
                    }

                    state.gestureEndAction(draggingStart)
                    launch {
                        rangeSliderLogic.activeInteraction(draggingStart).emit(finishInteraction)
                    }
                }
            }
        }
    } else {
        this
    }

private class RangeSliderLogic(
    val state: RangeSliderState,
    val startInteractionSource: MutableInteractionSource,
    val endInteractionSource: MutableInteractionSource,
) {
    fun activeInteraction(draggingStart: Boolean): MutableInteractionSource =
        if (draggingStart) startInteractionSource else endInteractionSource

    fun compareOffsets(eventX: Float): Int {
        val diffStart = abs(state.rawOffsetStart - eventX)
        val diffEnd = abs(state.rawOffsetEnd - eventX)
        return diffStart.compareTo(diffEnd)
    }

    fun captureThumb(
        draggingStart: Boolean,
        posX: Float,
        interaction: Interaction,
        scope: CoroutineScope,
    ) {
        state.onDrag(
            draggingStart,
            posX - if (draggingStart) state.rawOffsetStart else state.rawOffsetEnd,
        )
        scope.launch { activeInteraction(draggingStart).emit(interaction) }
    }
}

private fun Modifier.sliderSemantics(
    state: SliderState,
    enabled: Boolean,
): Modifier =
    semantics {
        if (!enabled) disabled()
        setProgress(
            action = { targetValue ->
                var newValue = targetValue.coerceIn(state.valueRange.start, state.valueRange.endInclusive)
                val originalVal = newValue
                val resolvedValue = if (state.steps > 0) {
                    var distance: Float = newValue
                    for (i in 0..state.steps + 1) {
                        val stepValue = lerp(
                            state.valueRange.start,
                            state.valueRange.endInclusive,
                            i.toFloat() / (state.steps + 1),
                        )
                        if (abs(stepValue - originalVal) <= distance) {
                            distance = abs(stepValue - originalVal)
                            newValue = stepValue
                        }
                    }
                    newValue
                } else {
                    newValue
                }

                // This is to keep it consistent with AbsSeekbar.java: return false if no
                // change from current.
                if (resolvedValue == state.value) {
                    false
                } else {
                    if (resolvedValue != state.value) {
                        if (state.onValueChange != null) {
                            state.onValueChange?.let { it(resolvedValue) }
                        } else {
                            state.value = resolvedValue
                        }
                    }
                    state.onValueChangeFinished?.invoke()
                    true
                }
            },
        )
    }.then(IncreaseHorizontalSemanticsBounds)
        .progressSemantics(
            state.value,
            state.valueRange.start..state.valueRange.endInclusive,
            state.steps,
        )

private fun snapValueToTick(
    current: Float,
    tickFractions: FloatArray,
    minPx: Float,
    maxPx: Float,
): Float =
    tickFractions
        .minByOrNull { abs(lerp(minPx, maxPx, it) - current) }
        ?.run { lerp(minPx, maxPx, this) } ?: current

private suspend fun AwaitPointerEventScope.awaitSlop(
    id: PointerId,
    type: PointerType,
): Pair<PointerInputChange, Float>? {
    var initialDelta = 0f
    val postPointerSlop = { pointerInput: PointerInputChange, offset: Float ->
        pointerInput.consume()
        initialDelta = offset
    }
    val afterSlopResult = awaitHorizontalPointerSlopOrCancellation(id, type, postPointerSlop)
    return if (afterSlopResult != null) afterSlopResult to initialDelta else null
}

private fun stepsToTickFractions(steps: Int): FloatArray =
    if (steps == 0) {
        floatArrayOf()
    } else {
        FloatArray(steps + 2) {
            it.toFloat() / (steps + 1)
        }
    }

// Scale x1 from a1..b1 range to a2..b2 range
private fun scale(
    a1: Float,
    b1: Float,
    x1: Float,
    a2: Float,
    b2: Float,
) = lerp(a2, b2, calcFraction(a1, b1, x1))

// Scale x.start, x.endInclusive from a1..b1 range to a2..b2 range
private fun scale(
    a1: Float,
    b1: Float,
    x: SliderRange,
    a2: Float,
    b2: Float,
) = SliderRange(scale(a1, b1, x.start, a2, b2), scale(a1, b1, x.endInclusive, a2, b2))

// Calculate the 0..1 fraction that `pos` value represents between `a` and `b`
private fun calcFraction(
    a: Float,
    b: Float,
    pos: Float,
) = (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)

@Immutable
public data class SliderStyle(
    val trackHeight: Dp = TrackHeight,
    val thumbWidth: Dp = ThumbWidth,
    val thumbHeight: Dp = ThumbHeight,
    val thumbSizeOnPress: DpSize = ThumbSizeOnPress,
    val trackInsideCornerSize: Dp = TrackInsideCornerSize,
    val thumbTrackGap: Dp = ThumbTrackGap,
    val trackTickSize: Dp = TrackTickSize,
    val thumbShape: Shape = ThumbShape,
)

@Immutable
public class SliderColors(
    public val thumbColor: Color,
    public val activeTrackColor: Color,
    public val activeTickColor: Color,
    public val inactiveTrackColor: Color,
    public val inactiveTickColor: Color,
    public val disabledThumbColor: Color,
    public val disabledActiveTrackColor: Color,
    public val disabledActiveTickColor: Color,
    public val disabledInactiveTrackColor: Color,
    public val disabledInactiveTickColor: Color,
) {
    public fun copy(
        thumbColor: Color = this.thumbColor,
        activeTrackColor: Color = this.activeTrackColor,
        activeTickColor: Color = this.activeTickColor,
        inactiveTrackColor: Color = this.inactiveTrackColor,
        inactiveTickColor: Color = this.inactiveTickColor,
        disabledThumbColor: Color = this.disabledThumbColor,
        disabledActiveTrackColor: Color = this.disabledActiveTrackColor,
        disabledActiveTickColor: Color = this.disabledActiveTickColor,
        disabledInactiveTrackColor: Color = this.disabledInactiveTrackColor,
        disabledInactiveTickColor: Color = this.disabledInactiveTickColor,
    ): SliderColors =
        SliderColors(
            thumbColor.takeOrElse { this.thumbColor },
            activeTrackColor.takeOrElse { this.activeTrackColor },
            activeTickColor.takeOrElse { this.activeTickColor },
            inactiveTrackColor.takeOrElse { this.inactiveTrackColor },
            inactiveTickColor.takeOrElse { this.inactiveTickColor },
            disabledThumbColor.takeOrElse { this.disabledThumbColor },
            disabledActiveTrackColor.takeOrElse { this.disabledActiveTrackColor },
            disabledActiveTickColor.takeOrElse { this.disabledActiveTickColor },
            disabledInactiveTrackColor.takeOrElse { this.disabledInactiveTrackColor },
            disabledInactiveTickColor.takeOrElse { this.disabledInactiveTickColor },
        )

    @Stable
    internal fun thumbColor(enabled: Boolean): Color = if (enabled) thumbColor else disabledThumbColor

    @Stable
    internal fun trackColor(
        enabled: Boolean,
        active: Boolean,
    ): Color =
        if (enabled) {
            if (active) activeTrackColor else inactiveTrackColor
        } else {
            if (active) disabledActiveTrackColor else disabledInactiveTrackColor
        }

    @Stable
    internal fun tickColor(
        enabled: Boolean,
        active: Boolean,
    ): Color =
        if (enabled) {
            if (active) activeTickColor else inactiveTickColor
        } else {
            if (active) disabledActiveTickColor else disabledInactiveTickColor
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SliderColors) return false

        if (thumbColor != other.thumbColor) return false
        if (activeTrackColor != other.activeTrackColor) return false
        if (activeTickColor != other.activeTickColor) return false
        if (inactiveTrackColor != other.inactiveTrackColor) return false
        if (inactiveTickColor != other.inactiveTickColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledActiveTrackColor != other.disabledActiveTrackColor) return false
        if (disabledActiveTickColor != other.disabledActiveTickColor) return false
        if (disabledInactiveTrackColor != other.disabledInactiveTrackColor) return false
        if (disabledInactiveTickColor != other.disabledInactiveTickColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + activeTrackColor.hashCode()
        result = 31 * result + activeTickColor.hashCode()
        result = 31 * result + inactiveTrackColor.hashCode()
        result = 31 * result + inactiveTickColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledActiveTrackColor.hashCode()
        result = 31 * result + disabledActiveTickColor.hashCode()
        result = 31 * result + disabledInactiveTrackColor.hashCode()
        result = 31 * result + disabledInactiveTickColor.hashCode()
        return result
    }
}

public class SliderState(
    value: Float = 0f,
    @param:IntRange(from = 0) public val steps: Int = 0,
    public var onValueChangeFinished: (() -> Unit)? = null,
    public val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) : DraggableState {
    private var valueState by mutableFloatStateOf(value)

    internal var onValueChange: ((Float) -> Unit)? = null

    public var value: Float
        set(newVal) {
            val coercedValue = newVal.coerceIn(valueRange.start, valueRange.endInclusive)
            val snappedValue = snapValueToTick(
                coercedValue,
                tickFractions,
                valueRange.start,
                valueRange.endInclusive,
            )
            valueState = snappedValue
        }
        get() = valueState

    override suspend fun drag(
        dragPriority: MutatePriority,
        block: suspend DragScope.() -> Unit,
    ): Unit =
        coroutineScope {
            isDragging = true
            scrollMutex.mutateWith(dragScope, dragPriority, block)
            isDragging = false
        }

    override fun dispatchRawDelta(delta: Float) {
        val maxPx = max(totalWidth - thumbWidth / 2, 0f)
        val minPx = min(thumbWidth / 2, maxPx)
        rawOffset = (rawOffset + delta + pressOffset)
        pressOffset = 0f
        val offsetInTrack = snapValueToTick(rawOffset, tickFractions, minPx, maxPx)
        val scaledUserValue = scaleToUserValue(minPx, maxPx, offsetInTrack)
        if (scaledUserValue != this.value) {
            if (onValueChange != null) {
                onValueChange?.let { it(scaledUserValue) }
            } else {
                this.value = scaledUserValue
            }
        }
    }

    internal val tickFractions = stepsToTickFractions(steps)
    private var totalWidth by mutableIntStateOf(0)
    internal var isRtl = false
    internal var trackHeight by mutableFloatStateOf(0f)
    internal var thumbWidth by mutableFloatStateOf(0f)

    internal val coercedValueAsFraction
        get() = calcFraction(
            valueRange.start,
            valueRange.endInclusive,
            value.coerceIn(valueRange.start, valueRange.endInclusive),
        )

    internal var isDragging by mutableStateOf(false)
        private set

    internal fun updateDimensions(
        newTrackHeight: Float,
        newTotalWidth: Int,
    ) {
        trackHeight = newTrackHeight
        totalWidth = newTotalWidth
    }

    internal val gestureEndAction = {
        if (!isDragging) {
            // check isDragging in case the change is still in progress (touch -> drag case)
            onValueChangeFinished?.invoke()
        }
    }

    internal fun onPress(pos: Offset) {
        val to = if (isRtl) totalWidth - pos.x else pos.x
        pressOffset = to - rawOffset
    }

    private var rawOffset by mutableFloatStateOf(scaleToOffset(value))
    private var pressOffset by mutableFloatStateOf(0f)
    private val dragScope: DragScope = object : DragScope {
        override fun dragBy(pixels: Float): Unit = dispatchRawDelta(pixels)
    }

    private val scrollMutex = MutatorMutex()

    private fun scaleToUserValue(
        minPx: Float,
        maxPx: Float,
        offset: Float,
    ) = scale(minPx, maxPx, offset, valueRange.start, valueRange.endInclusive)

    private fun scaleToOffset(userValue: Float) =
        scale(valueRange.start, valueRange.endInclusive, userValue, 0f, 0f)
}

public class RangeSliderState(
    activeRangeStart: Float = 0f,
    activeRangeEnd: Float = 1f,
    @param:IntRange(from = 0) public val steps: Int = 0,
    public var onValueChangeFinished: (() -> Unit)? = null,
    public val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    private var activeRangeStartState by mutableFloatStateOf(activeRangeStart)
    private var activeRangeEndState by mutableFloatStateOf(activeRangeEnd)

    internal var onValueChange: ((SliderRange) -> Unit)? = null

    internal var activeRangeStart: Float
        set(newVal) {
            val coercedValue = newVal.coerceIn(valueRange.start, activeRangeEnd)
            val snappedValue = snapValueToTick(
                coercedValue,
                tickFractions,
                valueRange.start,
                valueRange.endInclusive,
            )
            activeRangeStartState = snappedValue
        }
        get() = activeRangeStartState

    internal var activeRangeEnd: Float
        set(newVal) {
            val coercedValue = newVal.coerceIn(activeRangeStart, valueRange.endInclusive)
            val snappedValue = snapValueToTick(
                coercedValue,
                tickFractions,
                valueRange.start,
                valueRange.endInclusive,
            )
            activeRangeEndState = snappedValue
        }
        get() = activeRangeEndState

    internal val tickFractions = stepsToTickFractions(steps)

    internal var trackHeight by mutableFloatStateOf(0f)
    internal var startThumbWidth by mutableFloatStateOf(0f)
    internal var endThumbWidth by mutableFloatStateOf(0f)
    internal var totalWidth by mutableIntStateOf(0)
    internal var rawOffsetStart by mutableFloatStateOf(0f)
    internal var rawOffsetEnd by mutableFloatStateOf(0f)

    internal var isRtl by mutableStateOf(false)

    internal val gestureEndAction: (Boolean) -> Unit = { onValueChangeFinished?.invoke() }

    private var maxPx by mutableFloatStateOf(0f)
    private var minPx by mutableFloatStateOf(0f)

    internal fun onDrag(
        isStart: Boolean,
        offset: Float,
    ) {
        val offsetRange = if (isStart) {
            rawOffsetStart = (rawOffsetStart + offset)
            rawOffsetEnd = scaleToOffset(minPx, maxPx, activeRangeEnd)
            val offsetEnd = rawOffsetEnd
            var offsetStart = rawOffsetStart.coerceIn(minPx, offsetEnd)
            offsetStart = snapValueToTick(offsetStart, tickFractions, minPx, maxPx)
            SliderRange(offsetStart, offsetEnd)
        } else {
            rawOffsetEnd = (rawOffsetEnd + offset)
            rawOffsetStart = scaleToOffset(minPx, maxPx, activeRangeStart)
            val offsetStart = rawOffsetStart
            var offsetEnd = rawOffsetEnd.coerceIn(offsetStart, maxPx)
            offsetEnd = snapValueToTick(offsetEnd, tickFractions, minPx, maxPx)
            SliderRange(offsetStart, offsetEnd)
        }
        val scaledUserValue = scaleToUserValue(minPx, maxPx, offsetRange)
        if (scaledUserValue != SliderRange(activeRangeStart, activeRangeEnd)) {
            if (onValueChange != null) {
                onValueChange?.let { it(scaledUserValue) }
            } else {
                this.activeRangeStart = scaledUserValue.start
                this.activeRangeEnd = scaledUserValue.endInclusive
            }
        }
    }

    internal val coercedActiveRangeStartAsFraction
        get() = calcFraction(valueRange.start, valueRange.endInclusive, activeRangeStart)

    internal val coercedActiveRangeEndAsFraction
        get() = calcFraction(valueRange.start, valueRange.endInclusive, activeRangeEnd)

    internal val startSteps
        get() = floor(steps * coercedActiveRangeEndAsFraction).toInt()

    internal val endSteps
        get() = floor(steps * (1f - coercedActiveRangeStartAsFraction)).toInt()

    // scales range offset from within minPx..maxPx to within valueRange.start..valueRange.end
    private fun scaleToUserValue(
        minPx: Float,
        maxPx: Float,
        offset: SliderRange,
    ) = scale(minPx, maxPx, offset, valueRange.start, valueRange.endInclusive)

    // scales float userValue within valueRange.start..valueRange.end to within minPx..maxPx
    private fun scaleToOffset(
        minPx: Float,
        maxPx: Float,
        userValue: Float,
    ) = scale(valueRange.start, valueRange.endInclusive, userValue, minPx, maxPx)

    internal fun updateMinMaxPx() {
        val newMaxPx = max(totalWidth - endThumbWidth / 2, 0f)
        val newMinPx = min(startThumbWidth / 2, newMaxPx)
        if (minPx != newMinPx || maxPx != newMaxPx) {
            minPx = newMinPx
            maxPx = newMaxPx
            rawOffsetStart = scaleToOffset(minPx, maxPx, activeRangeStart)
            rawOffsetEnd = scaleToOffset(minPx, maxPx, activeRangeEnd)
        }
    }
}

@Immutable
@JvmInline
public value class SliderRange(
    public val packedValue: Long,
) {
    @Stable
    public val start: Float
        get() {
            // Explicitly compare against packed values to avoid auto-boxing of Size.Unspecified
            check(this.packedValue != Unspecified.packedValue) { "SliderRange is unspecified" }
            return unpackFloat1(packedValue)
        }

    @Stable
    public val endInclusive: Float
        get() {
            // Explicitly compare against packed values to avoid auto-boxing of Size.Unspecified
            check(this.packedValue != Unspecified.packedValue) { "SliderRange is unspecified" }
            return unpackFloat2(packedValue)
        }

    public companion object {
        @Stable
        public val Unspecified: SliderRange = SliderRange(Float.NaN, Float.NaN)
    }

    override fun toString(): String =
        if (isSpecified) {
            "$start..$endInclusive"
        } else {
            "FloatRange.Unspecified"
        }
}

@Stable
internal fun SliderRange(
    start: Float,
    endInclusive: Float,
): SliderRange {
    val isUnspecified = start.isNaN() && endInclusive.isNaN()

    require(isUnspecified || start <= endInclusive + SliderRangeTolerance) {
        "start($start) must be <= endInclusive($endInclusive)"
    }
    return SliderRange(packFloats(start, endInclusive))
}

@Stable
internal fun SliderRange(range: ClosedFloatingPointRange<Float>): SliderRange {
    val start = range.start
    val endInclusive = range.endInclusive
    val isUnspecified = start.isNaN() && endInclusive.isNaN()
    require(isUnspecified || start <= endInclusive + SliderRangeTolerance) {
        "ClosedFloatingPointRange<Float>.start($start) must " +
            "be <= ClosedFloatingPoint.endInclusive($endInclusive)"
    }
    return SliderRange(packFloats(start, endInclusive))
}

@Stable
internal val SliderRange.isSpecified: Boolean
    get() = packedValue != SliderRange.Unspecified.packedValue

private enum class SliderComponents {
    THUMB,
    TRACK,
}

private enum class RangeSliderComponents {
    ENDTHUMB,
    STARTTHUMB,
    TRACK,
}
