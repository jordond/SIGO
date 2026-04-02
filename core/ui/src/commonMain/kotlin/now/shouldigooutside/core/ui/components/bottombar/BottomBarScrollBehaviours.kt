package now.shouldigooutside.core.ui.components.bottombar

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

public class PinnedBottomBarScrollBehavior(
    override val state: BottomBarState,
    public val canScroll: () -> Boolean = { true },
) : BottomBarScrollBehavior {
    override val isPinned: Boolean = true
    override val snapAnimationSpec: AnimationSpec<Float>? = null
    override val flingAnimationSpec: DecayAnimationSpec<Float>? = null
    override var nestedScrollConnection: NestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!canScroll()) return Offset.Zero
                if (consumed.y == 0f && available.y > 0f) {
                    state.contentOffset = 0f
                } else {
                    state.contentOffset += consumed.y
                }
                return Offset.Zero
            }
        }
}

public class EnterAlwaysBottomBarScrollBehavior(
    override val state: BottomBarState,
    override val snapAnimationSpec: AnimationSpec<Float>?,
    override val flingAnimationSpec: DecayAnimationSpec<Float>?,
    public val canScroll: () -> Boolean = { true },
) : BottomBarScrollBehavior {
    override val isPinned: Boolean = false
    override var nestedScrollConnection: NestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (!canScroll()) return Offset.Zero
                state.contentOffset += consumed.y

                if (consumed.y != 0f) {
                    state.heightOffset -= consumed.y
                } else if (available.y > 0f) {
                    // When the child can't consume downward scroll anymore, keep revealing the bar.
                    state.heightOffset -= available.y
                    state.contentOffset = 0f
                }

                return Offset.Zero
            }

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity,
            ): Velocity {
                val superConsumed = super.onPostFling(consumed, available)
                return superConsumed +
                    settleBottomBar(
                        state = state,
                        velocity = available.y,
                        flingAnimationSpec = flingAnimationSpec,
                        snapAnimationSpec = snapAnimationSpec,
                    )
            }
        }
}

public suspend fun settleBottomBar(
    state: BottomBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?,
): Velocity {
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }

    var remainingVelocity = velocity

    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        ).animateDecay(flingAnimationSpec) {
            val delta = value - lastValue
            val initialHeightOffset = state.heightOffset
            state.heightOffset = initialHeightOffset - delta
            val consumed = abs(initialHeightOffset - state.heightOffset)
            lastValue = value
            remainingVelocity = this.velocity
            if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
        }
    }

    if (snapAnimationSpec != null) {
        if (state.heightOffset > 0f &&
            state.heightOffset < state.heightOffsetLimit
        ) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec,
            ) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}

@Stable
public interface BottomBarScrollBehavior {
    public val state: BottomBarState
    public val isPinned: Boolean
    public val snapAnimationSpec: AnimationSpec<Float>?
    public val flingAnimationSpec: DecayAnimationSpec<Float>?
    public val nestedScrollConnection: NestedScrollConnection
}
