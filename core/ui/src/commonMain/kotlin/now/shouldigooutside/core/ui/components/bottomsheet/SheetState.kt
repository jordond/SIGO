package now.shouldigooutside.core.ui.components.bottomsheet

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import now.shouldigooutside.core.ui.components.bottomsheet.SheetState.Companion.Saver
import now.shouldigooutside.core.ui.foundation.AnchoredDraggableState
import now.shouldigooutside.core.ui.foundation.animateTo
import now.shouldigooutside.core.ui.foundation.snapTo
import kotlin.jvm.JvmName

/**
 * State of a sheet composable, such as [BasicModalBottomSheet]
 *
 * Contains states relating to its swipe position as well as animations between state values.
 *
 * @param skipPartiallyExpanded Whether the partially expanded state, if the sheet is large enough,
 *   should be skipped. If true, the sheet will always expand to the [Expanded] state and move to
 *   the [Hidden] state if available when hiding the sheet, either programmatically or by user
 *   interaction.
 * @param positionalThreshold The positional threshold, in px, to be used when calculating the
 *   target state while a drag is in progress and when settling after the drag ends. This is the
 *   distance from the start of a transition. It will be, depending on the direction of the
 *   interaction, added or subtracted from/to the origin offset. It should always be a positive
 *   value.
 * @param velocityThreshold The velocity threshold (in px per second) that the end velocity has to
 *   exceed in order to animate to the next state, even if the [positionalThreshold] has not been
 *   reached.
 * @param initialValue The initial value of the state.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 * @param skipHiddenState Whether the hidden state should be skipped. If true, the sheet will always
 *   expand to the [Expanded] state and move to the [PartiallyExpanded] if available, either
 *   programmatically or by user interaction.
 */
@Stable
public class SheetState(
    internal val skipPartiallyExpanded: Boolean,
    positionalThreshold: () -> Float,
    velocityThreshold: () -> Float,
    initialValue: SheetValue = SheetValue.Hidden,
    confirmValueChange: (SheetValue) -> Boolean = { true },
    internal val skipHiddenState: Boolean = false,
) {
    init {
        if (skipPartiallyExpanded) {
            require(initialValue != SheetValue.PartiallyExpanded) {
                "The initial value must not be set to PartiallyExpanded if skipPartiallyExpanded " +
                    "is set to true."
            }
        }
        if (skipHiddenState) {
            require(initialValue != SheetValue.Hidden) {
                "The initial value must not be set to Hidden if skipHiddenState is set to true."
            }
        }
    }

    /**
     * The current value of the state.
     *
     * If no swipe or animation is in progress, this corresponds to the state the bottom sheet is
     * currently in. If a swipe or an animation is in progress, this corresponds the state the sheet
     * was in before the swipe or animation started.
     */
    public val currentValue: SheetValue
        get() = anchoredDraggableState.currentValue

    /**
     * The target value of the bottom sheet state.
     *
     * If a swipe is in progress, this is the value that the sheet would animate to if the swipe
     * finishes. If an animation is running, this is the target value of that animation. Finally, if
     * no swipe or animation is in progress, this is the same as the [currentValue].
     */
    public val targetValue: SheetValue
        get() = anchoredDraggableState.targetValue

    /** Whether the modal bottom sheet is visible. */
    public val isVisible: Boolean
        get() = anchoredDraggableState.currentValue != SheetValue.Hidden

    /**
     * Whether an expanding or collapsing sheet animation is currently in progress.
     *
     * See [expand], [partialExpand], [show] or [hide] for more information.
     */
    public val isAnimationRunning: Boolean
        get() = anchoredDraggableState.isAnimationRunning

    /**
     * Require the current offset (in pixels) of the bottom sheet.
     *
     * The offset will be initialized during the first measurement phase of the provided sheet
     * content.
     *
     * These are the phases: Composition { -> Effects } -> Layout { Measurement -> Placement } ->
     * Drawing
     *
     * During the first composition, an [IllegalStateException] is thrown. In subsequent
     * compositions, the offset will be derived from the anchors of the previous pass. Always prefer
     * accessing the offset from a LaunchedEffect as it will be scheduled to be executed the next
     * frame, after layout.
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     */
    public fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    /**
     * Calculate the progress of the bottom sheet's expansion state.
     * Returns a float value between 0 (hidden) and 1 (fully expanded).
     *
     * The progress calculation relies on the sheet's offset, which is initialized
     * during the first measurement phase of the provided sheet content.
     *
     * Note that during the first composition, the offset may not be initialized yet.
     * In such cases, this function will return 0f as a fallback value.
     *
     * For real-time progress tracking, this should be called within a LaunchedEffect or
     * similar composition-aware context to ensure proper updates during sheet animation
     * and user interaction.
     *
     * @return Float value between 0 and 1, where:
     *         0f = Sheet is fully hidden
     *         1f = Sheet is fully expanded
     *         Values between 0-1 represent partial expansion states
     * @throws IllegalStateException if called before the sheet has been measured (caught internally)
     */
    public fun calculateSheetProgress(): Float {
        try {
            val offset = requireOffset()
            val anchors = anchoredDraggableState.anchors
            val maxOffset = anchors.maxAnchor()
            val minOffset = anchors.minAnchor()

            return if (maxOffset == minOffset) {
                1f
            } else {
                (1f - (offset - minOffset) / (maxOffset - minOffset))
                    .coerceIn(0f, 1f)
            }
        } catch (e: Exception) {
            return 0f
        }
    }

    /** Whether the sheet has an expanded state defined. */
    public val hasExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasAnchorFor(SheetValue.Expanded)

    /** Whether the modal bottom sheet has a partially expanded state defined. */
    public val hasPartiallyExpandedState: Boolean
        get() = anchoredDraggableState.anchors.hasAnchorFor(SheetValue.PartiallyExpanded)

    /**
     * Fully expand the bottom sheet with animation and suspend until it is fully expanded or
     * animation has been cancelled.
     * *
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    public suspend fun expand() {
        anchoredDraggableState.animateTo(SheetValue.Expanded)
    }

    /**
     * Animate the bottom sheet and suspend until it is partially expanded or animation has been
     * cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     * @throws [IllegalStateException] if [skipPartiallyExpanded] is set to true
     */
    public suspend fun partialExpand() {
        check(!skipPartiallyExpanded) {
            "Attempted to animate to partial expanded when skipPartiallyExpanded was enabled. Set" +
                " skipPartiallyExpanded to false to use this function."
        }
        animateTo(SheetValue.PartiallyExpanded, showMotionSpec)
    }

    /**
     * Expand the bottom sheet with animation and suspend until it is [PartiallyExpanded] if defined
     * else [Expanded].
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    public suspend fun show() {
        val targetValue =
            when {
                hasPartiallyExpandedState -> SheetValue.PartiallyExpanded
                else -> SheetValue.Expanded
            }
        animateTo(targetValue, showMotionSpec)
    }

    /**
     * Hide the bottom sheet with animation and suspend until it is fully hidden or animation has
     * been cancelled.
     *
     * @throws [CancellationException] if the animation is interrupted
     */
    public suspend fun hide() {
        check(!skipHiddenState) {
            "Attempted to animate to hidden when skipHiddenState was enabled. Set skipHiddenState" +
                " to false to use this function."
        }
        animateTo(SheetValue.Hidden, hideMotionSpec)
    }

    /**
     * Animate to a [targetValue]. If the [targetValue] is not in the set of anchors, the
     * [currentValue] will be updated to the [targetValue] without updating the offset.
     *
     * @param targetValue The target value of the animation
     * @param animationSpec an [AnimationSpec]
     * @param velocity an initial velocity for the animation
     * @throws CancellationException if the interaction interrupted by another interaction like a
     *   gesture interaction or another programmatic interaction like a [animateTo] or [snapTo]
     *   call.
     */
    internal suspend fun animateTo(
        targetValue: SheetValue,
        animationSpec: FiniteAnimationSpec<Float>,
        velocity: Float = anchoredDraggableState.lastVelocity,
    ) {
        anchoredDraggableState.anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
            val targetOffset = anchors.positionOf(latestTarget)
            if (!targetOffset.isNaN()) {
                var prev = if (offset.isNaN()) 0f else offset
                animate(prev, targetOffset, velocity, animationSpec) { value, velocity ->
                    // Our onDrag coerces the value within the bounds, but an animation may
                    // overshoot, for example a spring animation or an overshooting interpolator
                    // We respect the user's intention and allow the overshoot, but still use
                    // DraggableState's drag for its mutex.
                    dragTo(value, velocity)
                    prev = value
                }
            }
        }
    }

    /**
     * Snap to a [targetValue] without any animation.
     *
     * @param targetValue The target value of the animation
     * @throws CancellationException if the interaction interrupted by another interaction like a
     *   gesture interaction or another programmatic interaction like a [animateTo] or [snapTo]
     *   call.
     */
    internal suspend fun snapTo(targetValue: SheetValue) {
        anchoredDraggableState.snapTo(targetValue)
    }

    /**
     * Find the closest anchor taking into account the velocity and settle at it with an animation.
     */
    internal suspend fun settle(velocity: Float) {
        anchoredDraggableState.settle(velocity)
    }

    internal var anchoredDraggableMotionSpec: AnimationSpec<Float> = BottomSheetAnimationSpec

    internal var anchoredDraggableState =
        AnchoredDraggableState(
            initialValue = initialValue,
            animationSpec = anchoredDraggableMotionSpec,
            confirmValueChange = confirmValueChange,
            positionalThreshold = { positionalThreshold() },
            velocityThreshold = velocityThreshold,
        )

    internal val offset: Float
        get() = anchoredDraggableState.offset

    internal var showMotionSpec: FiniteAnimationSpec<Float> = snap()

    internal var hideMotionSpec: FiniteAnimationSpec<Float> = snap()

    public companion object {
        /** The default [Saver] implementation for [SheetState]. */
        public fun Saver(
            skipPartiallyExpanded: Boolean,
            positionalThreshold: () -> Float,
            velocityThreshold: () -> Float,
            confirmValueChange: (SheetValue) -> Boolean,
            skipHiddenState: Boolean,
        ): Saver<SheetState, SheetValue> =
            Saver(
                save = { it.currentValue },
                restore = { savedValue ->
                    SheetState(
                        skipPartiallyExpanded,
                        positionalThreshold,
                        velocityThreshold,
                        savedValue,
                        confirmValueChange,
                        skipHiddenState,
                    )
                },
            )
    }
}

public enum class SheetValue {
    Hidden,
    Expanded,
    PartiallyExpanded,
}

@Stable
public object BottomSheetDefaults {
    public val ExpandedShape: Shape
        @Composable get() = RoundedCornerShape(16.dp)

    public val ContainerColor: Color
        @Composable get() = Color.White

    public val ScrimColor: Color
        @Composable get() = Color.Black.copy(alpha = 0.32f)

    public val SheetMaxWidth: Dp = 640.dp

    public val windowInsets: WindowInsets
        @Composable get() = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)

    internal val PositionalThreshold = 56.dp

    internal val VelocityThreshold = 125.dp
}

internal fun ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
    sheetState: SheetState,
    orientation: Orientation,
    onFling: (velocity: Float) -> Unit,
): NestedScrollConnection =
    object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            val delta = available.toFloat()
            return if (delta < 0 && source == NestedScrollSource.UserInput) {
                sheetState.anchoredDraggableState.dispatchRawDelta(delta).toOffset()
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset =
            if (source == NestedScrollSource.UserInput) {
                sheetState.anchoredDraggableState.dispatchRawDelta(available.toFloat()).toOffset()
            } else {
                Offset.Zero
            }

        override suspend fun onPreFling(available: Velocity): Velocity {
            val toFling = available.toFloat()
            val currentOffset = sheetState.requireOffset()
            val minAnchor = sheetState.anchoredDraggableState.anchors.minAnchor()
            return if (toFling < 0 && currentOffset > minAnchor) {
                onFling(toFling)
                // since we go to the anchor with tween settling, consume all for the best UX
                available
            } else {
                Velocity.Zero
            }
        }

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity,
        ): Velocity {
            onFling(available.toFloat())
            return available
        }

        private fun Float.toOffset(): Offset =
            Offset(
                x = if (orientation == Orientation.Horizontal) this else 0f,
                y = if (orientation == Orientation.Vertical) this else 0f,
            )

        @JvmName("velocityToFloat")
        private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

        @JvmName("offsetToFloat")
        private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
    }

@Composable
internal fun rememberSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
    initialValue: SheetValue = SheetValue.Hidden,
    skipHiddenState: Boolean = false,
    positionalThreshold: Dp = BottomSheetDefaults.PositionalThreshold,
    velocityThreshold: Dp = BottomSheetDefaults.VelocityThreshold,
): SheetState {
    val density = LocalDensity.current
    val positionalThresholdToPx = { with(density) { positionalThreshold.toPx() } }
    val velocityThresholdToPx = { with(density) { velocityThreshold.toPx() } }
    return rememberSaveable(
        skipPartiallyExpanded,
        confirmValueChange,
        skipHiddenState,
        saver =
            SheetState.Saver(
                skipPartiallyExpanded = skipPartiallyExpanded,
                positionalThreshold = positionalThresholdToPx,
                velocityThreshold = velocityThresholdToPx,
                confirmValueChange = confirmValueChange,
                skipHiddenState = skipHiddenState,
            ),
    ) {
        SheetState(
            skipPartiallyExpanded,
            positionalThresholdToPx,
            velocityThresholdToPx,
            initialValue,
            confirmValueChange,
            skipHiddenState,
        )
    }
}

/** A function that provides the default animation spec used by [SheetState]. */
private val BottomSheetAnimationSpec: AnimationSpec<Float> =
    tween(durationMillis = 300, easing = FastOutSlowInEasing)
