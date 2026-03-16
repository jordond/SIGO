package now.shouldigooutside.core.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BasicTooltipBox
import androidx.compose.foundation.BasicTooltipDefaults
import androidx.compose.foundation.BasicTooltipState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.window.PopupPositionProvider
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalContainerColor
import now.shouldigooutside.core.ui.components.TooltipDefaults.SpacingBetweenTooltipAndAnchor
import now.shouldigooutside.core.ui.preview.AppPreview

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun TooltipBox(
    modifier: Modifier = Modifier,
    positionProvider: PopupPositionProvider = rememberTooltipPositionProvider(),
    tooltip: @Composable TooltipScope.() -> Unit,
    state: TooltipState = rememberTooltipState(),
    focusable: Boolean = true,
    enableUserInput: Boolean = true,
    content: @Composable () -> Unit,
) {
    val transition = rememberTransition(state.transition, label = "tooltip transition")
    val anchorBounds: MutableState<LayoutCoordinates?> = remember { mutableStateOf(null) }
    val scope = remember { DefaultTooltipScope { anchorBounds.value } }

    val wrappedContent: @Composable () -> Unit = {
        Box(modifier = Modifier.onGloballyPositioned { anchorBounds.value = it }) {
            content()
        }
    }

    BasicTooltipBox(
        positionProvider = positionProvider,
        tooltip = {
            Box(Modifier.animateTooltip(transition)) {
                scope.tooltip()
            }
        },
        focusable = focusable,
        enableUserInput = enableUserInput,
        state = state,
        modifier = modifier,
        content = wrappedContent,
    )
}

@Composable
public fun TooltipScope.Tooltip(
    modifier: Modifier = Modifier,
    caretSize: DpSize = TooltipDefaults.CaretSize,
    maxWidth: Dp = TooltipDefaults.MaxWidth,
    shape: Shape = TooltipDefaults.Shape,
    containerColor: Color = LocalContainerColor.current,
    elevation: Dp = TooltipDefaults.ShadowElevation,
    content: @Composable () -> Unit,
) {
    val drawCaretModifier =
        if (caretSize.isUnspecified) {
            modifier
        } else {
            val density = LocalDensity.current
            val windowContainerWidthInPx = windowContainerWidthInPx()
            Modifier
                .drawCaret { anchorLayoutCoordinates ->
                    drawCaretWithPath(
                        density = density,
                        windowContainerWidthInPx = windowContainerWidthInPx,
                        containerColor = containerColor,
                        caretSize = caretSize,
                        anchorLayoutCoordinates = anchorLayoutCoordinates,
                    )
                }.then(modifier)
        }

    BrutalContainer(
        shape = shape,
        elevation = elevation,
    ) {
        Surface(
            modifier = drawCaretModifier,
            shape = shape,
            color = containerColor,
            shadowElevation = 0.dp,
        ) {
            Box(
                modifier =
                    Modifier
                        .sizeIn(
                            minWidth = TooltipDefaults.MinWidth,
                            maxWidth = maxWidth,
                            minHeight = TooltipDefaults.MinHeight,
                        ).padding(TooltipDefaults.ContentPadding),
            ) {
                content()
            }
        }
    }
}

public sealed interface TooltipScope {
    public fun Modifier.drawCaret(draw: CacheDrawScope.(LayoutCoordinates?) -> DrawResult): Modifier
}

internal class DefaultTooltipScope(
    val getAnchorBounds: () -> LayoutCoordinates?,
) : TooltipScope {
    override fun Modifier.drawCaret(draw: CacheDrawScope.(LayoutCoordinates?) -> DrawResult): Modifier =
        this.drawWithCache { draw(getAnchorBounds()) }
}

@OptIn(ExperimentalFoundationApi::class)
public interface TooltipState : BasicTooltipState {
    public val transition: MutableTransitionState<Boolean>
}

@Stable
private class TooltipStateImpl(
    initialIsVisible: Boolean,
    override val isPersistent: Boolean,
    private val mutatorMutex: MutatorMutex,
) : TooltipState {
    override val transition: MutableTransitionState<Boolean> = MutableTransitionState(initialIsVisible)
    private var job: CancellableContinuation<Unit>? = null

    override val isVisible: Boolean
        get() = transition.currentState || transition.targetState

    @OptIn(ExperimentalFoundationApi::class)
    override suspend fun show(mutatePriority: MutatePriority) {
        val cancellableShow: suspend () -> Unit = {
            suspendCancellableCoroutine { continuation ->
                transition.targetState = true
                job = continuation
            }
        }

        mutatorMutex.mutate(mutatePriority) {
            try {
                if (isPersistent) {
                    cancellableShow()
                } else {
                    withTimeout(BasicTooltipDefaults.TooltipDuration) { cancellableShow() }
                }
            } finally {
                if (mutatePriority != MutatePriority.PreventUserInput) {
                    dismiss()
                }
            }
        }
    }

    override fun dismiss() {
        transition.targetState = false
    }

    override fun onDispose() {
        job?.cancel()
    }
}

public object TooltipDefaults {
    public val CaretSize: DpSize = DpSize(12.dp, 6.dp)
    public val MaxWidth: Dp = 300.dp
    public val ShadowElevation: Dp = BrutalElevationDefaults.Small.default
    public val SpacingBetweenTooltipAndAnchor: Dp = 4.dp
    public val MinHeight: Dp = 24.dp
    public val MinWidth: Dp = 40.dp
    public val PlainTooltipVerticalPadding: Dp = 6.dp
    public val PlainTooltipHorizontalPadding: Dp = 12.dp
    public val ContentPadding: PaddingValues =
        PaddingValues(PlainTooltipHorizontalPadding, PlainTooltipVerticalPadding)
    public val Shape: RoundedCornerShape = RoundedCornerShape(4.dp)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun rememberTooltipState(
    initialIsVisible: Boolean = false,
    isPersistent: Boolean = false,
    mutatorMutex: MutatorMutex = BasicTooltipDefaults.GlobalMutatorMutex,
): TooltipState =
    remember(isPersistent, mutatorMutex) {
        TooltipStateImpl(
            initialIsVisible = initialIsVisible,
            isPersistent = isPersistent,
            mutatorMutex = mutatorMutex,
        )
    }

@Composable
public fun rememberTooltipPositionProvider(
    spacingBetweenTooltipAndAnchor: Dp = SpacingBetweenTooltipAndAnchor,
): PopupPositionProvider {
    val tooltipAnchorSpacing =
        with(LocalDensity.current) {
            spacingBetweenTooltipAndAnchor.roundToPx()
        }

    return remember(tooltipAnchorSpacing) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize,
            ): IntOffset {
                var x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                if (x < 0) {
                    x = anchorBounds.left
                } else if (x + popupContentSize.width > windowSize.width) {
                    x = anchorBounds.right - popupContentSize.width
                }

                var y = anchorBounds.top - popupContentSize.height - tooltipAnchorSpacing
                if (y < 0) y = anchorBounds.bottom + tooltipAnchorSpacing
                return IntOffset(x, y)
            }
        }
    }
}

internal fun Modifier.animateTooltip(transition: Transition<Boolean>): Modifier =
    composed(
        inspectorInfo =
            debugInspectorInfo {
                name = "animateTooltip"
                properties["transition"] = transition
            },
    ) {
        val inOutScaleAnimationSpec = tween<Float>(durationMillis = 100, easing = FastOutLinearInEasing)
        val inOutAlphaAnimationSpec = tween<Float>(durationMillis = 50, easing = FastOutSlowInEasing)

        val scale by transition.animateFloat(
            transitionSpec = { inOutScaleAnimationSpec },
            label = "tooltip transition: scaling",
            targetValueByState = { value -> if (value) 1f else 0.8f },
        )

        val alpha by transition.animateFloat(
            transitionSpec = { inOutAlphaAnimationSpec },
            label = "tooltip transition: alpha",
            targetValueByState = { value -> if (value) 1f else 0f },
        )

        this.graphicsLayer(scaleX = scale, scaleY = scale, alpha = alpha)
    }

private fun CacheDrawScope.drawCaretWithPath(
    density: Density,
    windowContainerWidthInPx: Int,
    containerColor: Color,
    caretSize: DpSize,
    anchorLayoutCoordinates: LayoutCoordinates?,
): DrawResult {
    val path = Path()

    if (anchorLayoutCoordinates != null) {
        val caretHeightPx: Int
        val caretWidthPx: Int
        val screenWidthPx: Int
        val tooltipAnchorSpacing: Int
        with(density) {
            caretHeightPx = caretSize.height.roundToPx()
            caretWidthPx = caretSize.width.roundToPx()
            screenWidthPx = windowContainerWidthInPx
            tooltipAnchorSpacing = SpacingBetweenTooltipAndAnchor.roundToPx()
        }
        val anchorBounds = anchorLayoutCoordinates.boundsInWindow()
        val anchorLeft = anchorBounds.left
        val anchorRight = anchorBounds.right
        val anchorTop = anchorBounds.top
        val anchorMid = (anchorRight + anchorLeft) / 2
        val anchorWidth = anchorRight - anchorLeft
        val tooltipWidth = this.size.width
        val tooltipHeight = this.size.height
        val isCaretTop = anchorTop - tooltipHeight - tooltipAnchorSpacing < 0
        val caretY =
            if (isCaretTop) {
                0f
            } else {
                tooltipHeight
            }

        // Default the caret to be in the middle
        // caret might need to be offset depending on where
        // the tooltip is placed relative to the anchor
        var position: Offset =
            if (anchorLeft - tooltipWidth / 2 + anchorWidth / 2 <= 0) {
                Offset(anchorMid, caretY)
            } else if (anchorRight + tooltipWidth / 2 - anchorWidth / 2 >= screenWidthPx) {
                val anchorMidFromRightScreenEdge = screenWidthPx - anchorMid
                val caretX = tooltipWidth - anchorMidFromRightScreenEdge
                Offset(caretX, caretY)
            } else {
                Offset(tooltipWidth / 2, caretY)
            }
        if (anchorMid - tooltipWidth / 2 < 0) {
            // The tooltip needs to be start aligned if it would collide with the left side of
            // screen.
            position = Offset(anchorMid - anchorLeft, caretY)
        } else if (anchorMid + tooltipWidth / 2 > screenWidthPx) {
            // The tooltip needs to be end aligned if it would collide with the right side of the
            // screen.
            position = Offset(anchorMid - (anchorRight - tooltipWidth), caretY)
        }

        if (isCaretTop) {
            path.apply {
                moveTo(x = position.x, y = position.y)
                lineTo(x = position.x + caretWidthPx / 2, y = position.y)
                lineTo(x = position.x, y = position.y - caretHeightPx)
                lineTo(x = position.x - caretWidthPx / 2, y = position.y)
                close()
            }
        } else {
            path.apply {
                moveTo(x = position.x, y = position.y)
                lineTo(x = position.x + caretWidthPx / 2, y = position.y)
                lineTo(x = position.x, y = position.y + caretHeightPx.toFloat())
                lineTo(x = position.x - caretWidthPx / 2, y = position.y)
                close()
            }
        }
    }

    return onDrawWithContent {
        if (anchorLayoutCoordinates != null) {
            drawContent()
            drawPath(path = path, color = containerColor)
        }
    }
}

// Won't be needing this once containerSize API in LocalWindowInfo is upstreamed.
@Composable
internal expect fun windowContainerWidthInPx(): Int

@Preview
@Composable
private fun TooltipPreview() {
    val scope = remember { DefaultTooltipScope { null } }
    AppPreview {
        with(scope) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp),
            ) {
                Tooltip(
                    containerColor = AppTheme.colors.primary,
                    caretSize = DpSize.Unspecified,
                ) {
                    Text(text = "Primary ToolTip")
                }

                Tooltip(
                    containerColor = AppTheme.colors.secondary,
                    caretSize = DpSize.Unspecified,
                ) {
                    Text(text = "Secondary ToolTip")
                }

                Tooltip(
                    containerColor = AppTheme.colors.tertiary,
                    caretSize = DpSize.Unspecified,
                ) {
                    Text(text = "Tertiary ToolTip")
                }
            }
        }
    }
}

@Preview
@Composable
private fun PlainTooltipWithCaret() {
    val scope = rememberCoroutineScope()
    AppPreview {
        val tooltipState = rememberTooltipState(true, isPersistent = true)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TooltipBox(
                tooltip = {
                    Tooltip(
                        containerColor = AppTheme.colors.primary,
                    ) {
                        Text(
                            text = "This is a tooltip",
                        )
                    }
                },
                state = tooltipState,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(Color.Blue)
                            .clickable(
                                onClick = {
                                    scope.launch { tooltipState.show() }
                                },
                            ),
                )
            }
        }
    }
}
