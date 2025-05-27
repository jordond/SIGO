package app.sigot.core.ui.foundation

import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

internal interface DraggableAnchors<T> {
    fun positionOf(value: T): Float

    fun hasAnchorFor(value: T): Boolean

    fun closestAnchor(position: Float): T?

    fun closestAnchor(
        position: Float,
        searchUpwards: Boolean,
    ): T?

    fun minAnchor(): Float

    fun maxAnchor(): Float

    val size: Int
}

internal class DraggableAnchorsConfig<T> {
    internal val anchors = mutableMapOf<T, Float>()

    infix fun T.at(position: Float) {
        anchors[this] = position
    }
}

internal fun <T : Any> DraggableAnchors(builder: DraggableAnchorsConfig<T>.() -> Unit): DraggableAnchors<T> =
    MapDraggableAnchors(DraggableAnchorsConfig<T>().apply(builder).anchors)

internal fun <T> Modifier.anchoredDraggable(
    state: AnchoredDraggableState<T>,
    orientation: Orientation,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    startDragImmediately: Boolean = state.isAnimationRunning,
) = draggable(
    state = state.draggableState,
    orientation = orientation,
    enabled = enabled,
    interactionSource = interactionSource,
    reverseDirection = reverseDirection,
    startDragImmediately = startDragImmediately,
    onDragStopped = { velocity -> launch { state.settle(velocity) } },
)

internal interface AnchoredDragScope {
    fun dragTo(
        newOffset: Float,
        lastKnownVelocity: Float = 0f,
    )
}

@Stable
internal class AnchoredDraggableState<T>(
    initialValue: T,
    val positionalThreshold: (totalDistance: Float) -> Float,
    val velocityThreshold: () -> Float,
    val animationSpec: AnimationSpec<Float>,
    val confirmValueChange: (newValue: T) -> Boolean = { true },
) {
    constructor(
        initialValue: T,
        anchors: DraggableAnchors<T>,
        positionalThreshold: (totalDistance: Float) -> Float,
        velocityThreshold: () -> Float,
        animationSpec: AnimationSpec<Float>,
        confirmValueChange: (newValue: T) -> Boolean = { true },
    ) : this(
        initialValue,
        positionalThreshold,
        velocityThreshold,
        animationSpec,
        confirmValueChange,
    ) {
        this.anchors = anchors
        trySnapTo(initialValue)
    }

    private val dragMutex = InternalMutatorMutex()

    val draggableState = object : DraggableState {
        private val dragScope = object : DragScope {
            override fun dragBy(pixels: Float) {
                with(anchoredDragScope) { dragTo(newOffsetForDelta(pixels)) }
            }
        }

        override suspend fun drag(
            dragPriority: MutatePriority,
            block: suspend DragScope.() -> Unit,
        ) {
            this@AnchoredDraggableState.anchoredDrag(dragPriority) {
                with(dragScope) { block() }
            }
        }

        override fun dispatchRawDelta(delta: Float) {
            this@AnchoredDraggableState.dispatchRawDelta(delta)
        }
    }

    var currentValue: T by mutableStateOf(initialValue)
        private set

    val targetValue: T by derivedStateOf {
        dragTarget ?: run {
            val currentOffset = offset
            if (!currentOffset.isNaN()) {
                computeTarget(currentOffset, currentValue, velocity = 0f)
            } else {
                currentValue
            }
        }
    }

    val closestValue: T by derivedStateOf {
        dragTarget ?: run {
            val currentOffset = offset
            if (!currentOffset.isNaN()) {
                computeTargetWithoutThresholds(currentOffset, currentValue)
            } else {
                currentValue
            }
        }
    }

    var offset: Float by mutableFloatStateOf(Float.NaN)
        private set

    fun requireOffset(): Float {
        check(!offset.isNaN()) {
            "The offset was read before being initialized. Did you access the offset in a phase " +
                "before layout, like effects or composition?"
        }
        return offset
    }

    val isAnimationRunning: Boolean
        get() = dragTarget != null

    @get:FloatRange(from = 0.0, to = 1.0)
    val progress: Float by derivedStateOf(structuralEqualityPolicy()) {
        val a = anchors.positionOf(currentValue)
        val b = anchors.positionOf(closestValue)
        val distance = abs(b - a)
        if (!distance.isNaN() && distance > 1e-6f) {
            val progress = (this.requireOffset() - a) / (b - a)
            if (progress < 1e-6f) {
                0f
            } else if (progress > 1 - 1e-6f) {
                1f
            } else {
                progress
            }
        } else {
            1f
        }
    }

    var lastVelocity: Float by mutableFloatStateOf(0f)
        private set

    private var dragTarget: T? by mutableStateOf(null)

    var anchors: DraggableAnchors<T> by mutableStateOf(emptyDraggableAnchors())
        private set

    fun updateAnchors(
        newAnchors: DraggableAnchors<T>,
        newTarget: T = if (!offset.isNaN()) {
            newAnchors.closestAnchor(offset) ?: targetValue
        } else {
            targetValue
        },
    ) {
        if (anchors != newAnchors) {
            anchors = newAnchors
            val snapSuccessful = trySnapTo(newTarget)
            if (!snapSuccessful) {
                dragTarget = newTarget
            }
        }
    }

    suspend fun settle(velocity: Float) {
        val previousValue = this.currentValue
        val targetValue = computeTarget(
            offset = requireOffset(),
            currentValue = previousValue,
            velocity = velocity,
        )
        if (confirmValueChange(targetValue)) {
            animateTo(targetValue, velocity)
        } else {
            animateTo(previousValue, velocity)
        }
    }

    private fun computeTarget(
        offset: Float,
        currentValue: T,
        velocity: Float,
    ): T {
        val currentAnchors = anchors
        val currentAnchorPosition = currentAnchors.positionOf(currentValue)
        val velocityThresholdPx = velocityThreshold()
        return if (currentAnchorPosition == offset || currentAnchorPosition.isNaN()) {
            currentValue
        } else if (currentAnchorPosition < offset) {
            if (velocity >= velocityThresholdPx) {
                currentAnchors.closestAnchor(offset, true)!!
            } else {
                val upper = currentAnchors.closestAnchor(offset, true)!!
                val distance = abs(currentAnchors.positionOf(upper) - currentAnchorPosition)
                val relativeThreshold = abs(positionalThreshold(distance))
                val absoluteThreshold = abs(currentAnchorPosition + relativeThreshold)
                if (offset < absoluteThreshold) currentValue else upper
            }
        } else {
            if (velocity <= -velocityThresholdPx) {
                currentAnchors.closestAnchor(offset, false)!!
            } else {
                val lower = currentAnchors.closestAnchor(offset, false)!!
                val distance = abs(currentAnchorPosition - currentAnchors.positionOf(lower))
                val relativeThreshold = abs(positionalThreshold(distance))
                val absoluteThreshold = abs(currentAnchorPosition - relativeThreshold)
                if (offset < 0) {
                    if (abs(offset) < absoluteThreshold) currentValue else lower
                } else {
                    if (offset > absoluteThreshold) currentValue else lower
                }
            }
        }
    }

    private fun computeTargetWithoutThresholds(
        offset: Float,
        currentValue: T,
    ): T {
        val currentAnchors = anchors
        val currentAnchorPosition = currentAnchors.positionOf(currentValue)
        return if (currentAnchorPosition == offset || currentAnchorPosition.isNaN()) {
            currentValue
        } else if (currentAnchorPosition < offset) {
            currentAnchors.closestAnchor(offset, true) ?: currentValue
        } else {
            currentAnchors.closestAnchor(offset, false) ?: currentValue
        }
    }

    private val anchoredDragScope: AnchoredDragScope = object : AnchoredDragScope {
        override fun dragTo(
            newOffset: Float,
            lastKnownVelocity: Float,
        ) {
            offset = newOffset
            lastVelocity = lastKnownVelocity
        }
    }

    suspend fun anchoredDrag(
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend AnchoredDragScope.(anchors: DraggableAnchors<T>) -> Unit,
    ) {
        try {
            dragMutex.mutate(dragPriority) {
                restartable(inputs = { anchors }) { latestAnchors ->
                    anchoredDragScope.block(latestAnchors)
                }
            }
        } finally {
            val closest = anchors.closestAnchor(offset)
            if (closest != null &&
                abs(offset - anchors.positionOf(closest)) <= 0.5f &&
                confirmValueChange.invoke(
                    closest,
                )
            ) {
                currentValue = closest
            }
        }
    }

    suspend fun anchoredDrag(
        targetValue: T,
        dragPriority: MutatePriority = MutatePriority.Default,
        block: suspend AnchoredDragScope.(anchors: DraggableAnchors<T>, targetValue: T) -> Unit,
    ) {
        if (anchors.hasAnchorFor(targetValue)) {
            try {
                dragMutex.mutate(dragPriority) {
                    dragTarget = targetValue
                    restartable(inputs = {
                        anchors to this@AnchoredDraggableState.targetValue
                    }) { (latestAnchors, latestTarget) ->
                        anchoredDragScope.block(latestAnchors, latestTarget)
                    }
                }
            } finally {
                dragTarget = null
                val closest = anchors.closestAnchor(offset)
                if (closest != null &&
                    abs(offset - anchors.positionOf(closest)) <= 0.5f &&
                    confirmValueChange.invoke(
                        closest,
                    )
                ) {
                    currentValue = closest
                }
            }
        } else {
            currentValue = targetValue
        }
    }

    internal fun newOffsetForDelta(delta: Float) =
        ((if (offset.isNaN()) 0f else offset) + delta).coerceIn(
            anchors.minAnchor(),
            anchors.maxAnchor(),
        )

    fun dispatchRawDelta(delta: Float): Float {
        val newOffset = newOffsetForDelta(delta)
        val oldOffset = if (offset.isNaN()) 0f else offset
        offset = newOffset
        return newOffset - oldOffset
    }

    private fun trySnapTo(targetValue: T): Boolean =
        dragMutex.tryMutate {
            with(anchoredDragScope) {
                val targetOffset = anchors.positionOf(targetValue)
                if (!targetOffset.isNaN()) {
                    dragTo(targetOffset)
                    dragTarget = null
                }
                currentValue = targetValue
            }
        }

    companion object {
        fun <T : Any> Saver(
            animationSpec: AnimationSpec<Float>,
            confirmValueChange: (T) -> Boolean,
            positionalThreshold: (distance: Float) -> Float,
            velocityThreshold: () -> Float,
        ) = Saver<AnchoredDraggableState<T>, T>(
            save = { it.currentValue },
            restore = {
                AnchoredDraggableState(
                    initialValue = it,
                    animationSpec = animationSpec,
                    confirmValueChange = confirmValueChange,
                    positionalThreshold = positionalThreshold,
                    velocityThreshold = velocityThreshold,
                )
            },
        )
    }
}

internal suspend fun <T> AnchoredDraggableState<T>.snapTo(targetValue: T) {
    anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
        val targetOffset = anchors.positionOf(latestTarget)
        if (!targetOffset.isNaN()) dragTo(targetOffset)
    }
}

internal suspend fun <T> AnchoredDraggableState<T>.animateTo(
    targetValue: T,
    velocity: Float = this.lastVelocity,
) {
    anchoredDrag(targetValue = targetValue) { anchors, latestTarget ->
        val targetOffset = anchors.positionOf(latestTarget)
        if (!targetOffset.isNaN()) {
            var prev = if (offset.isNaN()) 0f else offset
            animate(prev, targetOffset, velocity, animationSpec) { value, velocity ->
                dragTo(value, velocity)
                prev = value
            }
        }
    }
}

@Stable
internal object AnchoredDraggableDefaults {
    val AnimationSpec = SpringSpec<Float>()
}

internal class AnchoredDragFinishedSignal : CancellationException("Anchored drag finished")

private suspend fun <I> restartable(
    inputs: () -> I,
    block: suspend (I) -> Unit,
) {
    try {
        coroutineScope {
            var previousDrag: Job? = null
            snapshotFlow(inputs).collect { latestInputs ->
                previousDrag?.apply {
                    cancel(AnchoredDragFinishedSignal())
                    join()
                }
                previousDrag = launch(start = CoroutineStart.UNDISPATCHED) {
                    block(latestInputs)
                    this@coroutineScope.cancel(AnchoredDragFinishedSignal())
                }
            }
        }
    } catch (_: AnchoredDragFinishedSignal) {
        // Ignored
    }
}

private fun <T> emptyDraggableAnchors() = MapDraggableAnchors<T>(emptyMap())

private class MapDraggableAnchors<T>(
    private val anchors: Map<T, Float>,
) : DraggableAnchors<T> {
    override fun positionOf(value: T): Float = anchors[value] ?: Float.NaN

    override fun hasAnchorFor(value: T) = anchors.containsKey(value)

    override fun closestAnchor(position: Float): T? = anchors.minByOrNull { abs(position - it.value) }?.key

    override fun closestAnchor(
        position: Float,
        searchUpwards: Boolean,
    ): T? =
        anchors
            .minByOrNull { (_, anchor) ->
                val delta = if (searchUpwards) anchor - position else position - anchor
                if (delta < 0) Float.POSITIVE_INFINITY else delta
            }?.key

    override fun minAnchor() = anchors.values.minOrNull() ?: Float.NaN

    override fun maxAnchor() = anchors.values.maxOrNull() ?: Float.NaN

    override val size: Int
        get() = anchors.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapDraggableAnchors<*>) return false
        return anchors == other.anchors
    }

    override fun hashCode() = 31 * anchors.hashCode()

    override fun toString() = "MapDraggableAnchors($anchors)"
}

internal fun <T> Modifier.draggableAnchors(
    state: AnchoredDraggableState<T>,
    orientation: Orientation,
    anchors: (size: IntSize, constraints: Constraints) -> Pair<DraggableAnchors<T>, T>,
) = this then DraggableAnchorsElement(state, anchors, orientation)

private class DraggableAnchorsElement<T>(
    private val state: AnchoredDraggableState<T>,
    private val anchors: (size: IntSize, constraints: Constraints) -> Pair<DraggableAnchors<T>, T>,
    private val orientation: Orientation,
) : ModifierNodeElement<DraggableAnchorsNode<T>>() {
    override fun create() = DraggableAnchorsNode(state, anchors, orientation)

    override fun update(node: DraggableAnchorsNode<T>) {
        node.state = state
        node.anchors = anchors
        node.orientation = orientation
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DraggableAnchorsElement<*>) return false
        if (state != other.state) return false
        if (anchors !== other.anchors) return false
        if (orientation != other.orientation) return false
        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + anchors.hashCode()
        result = 31 * result + orientation.hashCode()
        return result
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            properties["state"] = state
            properties["anchors"] = anchors
            properties["orientation"] = orientation
        }
    }
}

private class DraggableAnchorsNode<T>(
    var state: AnchoredDraggableState<T>,
    var anchors: (size: IntSize, constraints: Constraints) -> Pair<DraggableAnchors<T>, T>,
    var orientation: Orientation,
) : Modifier.Node(),
    LayoutModifierNode {
    private var didLookahead: Boolean = false

    override fun onDetach() {
        didLookahead = false
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        if (!isLookingAhead || !didLookahead) {
            val size = IntSize(placeable.width, placeable.height)
            val newAnchorResult = anchors(size, constraints)
            state.updateAnchors(newAnchorResult.first, newAnchorResult.second)
        }
        didLookahead = isLookingAhead || didLookahead
        return layout(placeable.width, placeable.height) {
            val offset = if (isLookingAhead) {
                state.anchors.positionOf(state.targetValue)
            } else {
                state.requireOffset()
            }
            val xOffset = if (orientation == Orientation.Horizontal) offset else 0f
            val yOffset = if (orientation == Orientation.Vertical) offset else 0f
            placeable.place(xOffset.roundToInt(), yOffset.roundToInt())
        }
    }
}
