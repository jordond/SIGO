package now.shouldigooutside.core.ui.components.bottombar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
public fun BottomBarLayout(
    modifier: Modifier = Modifier,
    scrollBehavior: BottomBarScrollBehavior? = null,
    content: @Composable () -> Unit,
) {
    val height = remember { mutableFloatStateOf(0f) }

    SideEffect {
        if (scrollBehavior?.state?.heightOffsetLimit != height.floatValue) {
            scrollBehavior?.state?.heightOffsetLimit = height.floatValue
        }
    }

    val bottomBarDragModifier =
        if (scrollBehavior != null && !scrollBehavior.isPinned) {
            Modifier.draggable(
                orientation = Orientation.Vertical,
                state =
                    rememberDraggableState {
                        scrollBehavior.state.heightOffset -= it
                    },
                onDragStopped = { velocity ->
                    settleBottomBar(
                        state = scrollBehavior.state,
                        velocity = velocity,
                        flingAnimationSpec = scrollBehavior.flingAnimationSpec,
                        snapAnimationSpec = scrollBehavior.snapAnimationSpec,
                    )
                },
            )
        } else {
            Modifier
        }

    val dynamicHeight = (height.floatValue - (scrollBehavior?.state?.heightOffset ?: 0f))
        .toInt()
        .coerceAtLeast(0)

    Layout(
        content = content,
        modifier = modifier
            .then(bottomBarDragModifier)
            .clipToBounds(),
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        if (placeables.isEmpty() || placeables.size > 1) {
            throw IllegalStateException("BottomBarLayout expects one child!")
        }

        if (height.floatValue == 0f) {
            height.floatValue = placeables.first().height.toFloat()
        }

        layout(constraints.maxWidth, dynamicHeight) {
            placeables.first().placeRelative(0, 0)
        }
    }
}

public object BottomBarDefaults {
    public val BottomBarHeight: Dp = 95.dp

    @Composable
    public fun pinnedScrollBehavior(
        state: BottomBarState = rememberBottomBarState(),
        canScroll: () -> Boolean = { true },
    ): BottomBarScrollBehavior = PinnedBottomBarScrollBehavior(state = state, canScroll = canScroll)

    @Composable
    public fun enterAlwaysScrollBehavior(
        state: BottomBarState = rememberBottomBarState(),
        canScroll: () -> Boolean = { true },
        snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
        flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay(),
    ): BottomBarScrollBehavior =
        EnterAlwaysBottomBarScrollBehavior(
            state = state,
            snapAnimationSpec = snapAnimationSpec,
            flingAnimationSpec = flingAnimationSpec,
            canScroll = canScroll,
        )
}

@Composable
public fun rememberBottomBarState(
    initialHeightOffsetLimit: Float = Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f,
): BottomBarState =
    rememberSaveable(saver = BottomBarState.Saver) {
        BottomBarState(
            initialHeightOffsetLimit = initialHeightOffsetLimit,
            initialHeightOffset = initialHeightOffset,
            initialContentOffset = initialContentOffset,
        )
    }

@Stable
public class BottomBarState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float,
) {
    public var heightOffsetLimit: Float by mutableFloatStateOf(initialHeightOffsetLimit)

    public var heightOffset: Float
        get() = _heightOffset.floatValue
        set(newOffset) {
            _heightOffset.floatValue =
                newOffset.coerceIn(
                    minimumValue = 0f,
                    maximumValue = heightOffsetLimit,
                )
        }

    public var contentOffset: Float by mutableFloatStateOf(initialContentOffset)

    public val collapsedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                heightOffset / heightOffsetLimit
            } else {
                0f
            }

    public companion object {
        public val Saver: Saver<BottomBarState, *> =
            listSaver(
                save = {
                    listOf(
                        it.heightOffsetLimit,
                        it.heightOffset,
                        it.contentOffset,
                    )
                },
                restore = {
                    BottomBarState(
                        initialHeightOffsetLimit = it[0],
                        initialHeightOffset = it[1],
                        initialContentOffset = it[2],
                    )
                },
            )
    }

    private var _heightOffset = mutableFloatStateOf(initialHeightOffset)
}
