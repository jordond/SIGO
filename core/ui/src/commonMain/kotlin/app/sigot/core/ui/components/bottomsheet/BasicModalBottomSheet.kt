package app.sigot.core.ui.components.bottomsheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import app.sigot.core.ui.components.BottomSheetDefaults
import app.sigot.core.ui.components.bottomsheet.SheetValue.Expanded
import app.sigot.core.ui.components.bottomsheet.SheetValue.Hidden
import app.sigot.core.ui.components.bottomsheet.SheetValue.PartiallyExpanded
import app.sigot.core.ui.components.brutalBorder
import app.sigot.core.ui.foundation.DraggableAnchors
import app.sigot.core.ui.foundation.draggableAnchors
import com.nomanr.composables.internal.MotionSchemeKeyTokens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
public fun BasicModalBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.parent.SheetMaxWidth,
    sheetGesturesEnabled: Boolean = true,
    shape: Shape = BottomSheetDefaults.parent.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.parent.ContainerColor,
    scrimColor: Color = BottomSheetDefaults.parent.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = null,
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.parent.windowInsets },
    properties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
    border: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val anchoredDraggableMotion: FiniteAnimationSpec<Float> = MotionSchemeKeyTokens.DefaultSpatial.value()
    val showMotion: FiniteAnimationSpec<Float> = MotionSchemeKeyTokens.DefaultSpatial.value()
    val hideMotion: FiniteAnimationSpec<Float> = MotionSchemeKeyTokens.FastEffects.value()

    SideEffect {
        sheetState.showMotionSpec = showMotion
        sheetState.hideMotionSpec = hideMotion
        sheetState.anchoredDraggableMotionSpec = anchoredDraggableMotion
    }
    val scope = rememberCoroutineScope()
    val animateToDismiss: () -> Unit = {
        if (sheetState.anchoredDraggableState.confirmValueChange(Hidden)) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest()
                }
            }
        }
    }
    val settleToDismiss: (velocity: Float) -> Unit = {
        scope
            .launch { sheetState.settle(it) }
            .invokeOnCompletion { if (!sheetState.isVisible) onDismissRequest() }
    }

    val predictiveBackProgress = remember { Animatable(initialValue = 0f) }

    ModalBottomSheetDialog(
        properties = properties,
        onDismissRequest = {
            if (sheetState.currentValue == Expanded && sheetState.hasPartiallyExpandedState) {
                // Smoothly animate away predictive back transformations since we are not fully
                // dismissing. We don't need to do this in the else below because we want to
                // preserve the predictive back transformations (scale) during the hide animation.
                scope.launch { predictiveBackProgress.animateTo(0f) }
                scope.launch { sheetState.partialExpand() }
            } else { // Is expanded without collapsed state or is collapsed.
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
            }
        },
        predictiveBackProgress = predictiveBackProgress,
    ) {
        Box(modifier = Modifier.fillMaxSize().imePadding().semantics { isTraversalGroup = true }) {
            Scrim(
                color = scrimColor,
                onDismissRequest = animateToDismiss,
                visible = sheetState.targetValue != Hidden,
            )
            ModalBottomSheetContent(
                predictiveBackProgress = predictiveBackProgress,
                scope = scope,
                animateToDismiss = animateToDismiss,
                settleToDismiss = settleToDismiss,
                modifier = modifier,
                sheetState = sheetState,
                sheetMaxWidth = sheetMaxWidth,
                sheetGesturesEnabled = sheetGesturesEnabled,
                shape = shape,
                containerColor = containerColor,
                dragHandle = dragHandle,
                contentWindowInsets = contentWindowInsets,
                content = content,
            )
        }
    }
    if (sheetState.hasExpandedState) {
        LaunchedEffect(sheetState) { sheetState.show() }
    }
}

@Composable
internal fun BoxScope.ModalBottomSheetContent(
    predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    scope: CoroutineScope,
    animateToDismiss: () -> Unit,
    settleToDismiss: (velocity: Float) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.parent.SheetMaxWidth,
    sheetGesturesEnabled: Boolean = true,
    shape: Shape = BottomSheetDefaults.parent.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.parent.ContainerColor,
    dragHandle: @Composable (() -> Unit)?,
    border: Boolean = true,
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.parent.windowInsets },
    content: @Composable ColumnScope.() -> Unit,
) {
    val bottomSheetPaneTitle = "Bottom Sheet Pane Title"

    Box(
        modifier = modifier
            .align(Alignment.TopCenter)
            .widthIn(max = sheetMaxWidth)
            .fillMaxWidth()
            .then(
                if (sheetGesturesEnabled) {
                    Modifier.nestedScroll(
                        remember(sheetState) {
                            ConsumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                                sheetState = sheetState,
                                orientation = Orientation.Vertical,
                                onFling = settleToDismiss,
                            )
                        },
                    )
                } else {
                    Modifier
                },
            ).draggableAnchors(
                sheetState.anchoredDraggableState,
                Orientation.Vertical,
            ) { sheetSize, constraints ->
                val fullHeight = constraints.maxHeight.toFloat()
                val newAnchors = DraggableAnchors {
                    Hidden at fullHeight
                    if (sheetSize.height > (fullHeight / 2) && !sheetState.skipPartiallyExpanded) {
                        PartiallyExpanded at fullHeight / 2f
                    }
                    if (sheetSize.height != 0) {
                        Expanded at max(0f, fullHeight - sheetSize.height)
                    }
                }
                val newTarget = when (sheetState.anchoredDraggableState.targetValue) {
                    Hidden -> Hidden
                    PartiallyExpanded, Expanded -> {
                        val hasPartiallyExpandedState = newAnchors.hasAnchorFor(PartiallyExpanded)
                        val newTarget = if (hasPartiallyExpandedState) {
                            PartiallyExpanded
                        } else if (newAnchors.hasAnchorFor(Expanded)) {
                            Expanded
                        } else {
                            Hidden
                        }
                        newTarget
                    }
                }
                return@draggableAnchors newAnchors to newTarget
            }.draggable(
                state = sheetState.anchoredDraggableState.draggableState,
                orientation = Orientation.Vertical,
                enabled = sheetGesturesEnabled && sheetState.isVisible,
                startDragImmediately = sheetState.anchoredDraggableState.isAnimationRunning,
                onDragStopped = { settleToDismiss(it) },
            ).semantics {
                paneTitle = bottomSheetPaneTitle
                traversalIndex = 0f
            }.graphicsLayer {
                val sheetOffset = sheetState.anchoredDraggableState.offset
                val sheetHeight = size.height
                if (!sheetOffset.isNaN() && !sheetHeight.isNaN() && sheetHeight != 0f) {
                    val progress = predictiveBackProgress.value
                    scaleX = calculatePredictiveBackScaleX(progress)
                    scaleY = calculatePredictiveBackScaleY(progress)
                    transformOrigin = TransformOrigin(0.5f, (sheetOffset + sheetHeight) / sheetHeight)
                }
            }
            // Scale up the Surface vertically in case the sheet's offset overflows below the
            // min anchor. This is done to avoid showing a gap when the sheet opens and bounces
            // when it's applied with a bouncy motion. Note that the content inside the Surface
            // is scaled back down to maintain its aspect ratio (see below).
            .verticalScaleUp(sheetState),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    color = containerColor,
                    shape = shape,
                ).then(
                    if (border) Modifier.brutalBorder(shape = shape) else Modifier,
                ).windowInsetsPadding(contentWindowInsets())
                .graphicsLayer {
                    val progress = predictiveBackProgress.value
                    val predictiveBackScaleX = calculatePredictiveBackScaleX(progress)
                    val predictiveBackScaleY = calculatePredictiveBackScaleY(progress)

                    // Preserve the original aspect ratio and alignment of the child content.
                    scaleY = if (predictiveBackScaleY != 0f) {
                        predictiveBackScaleX / predictiveBackScaleY
                    } else {
                        1f
                    }
                    transformOrigin = PredictiveBackChildTransformOrigin
                }
                // Scale the content down in case the sheet offset overflows below the min anchor.
                // The wrapping Surface is scaled up, so this is done to maintain the content's
                // aspect ratio.
                .verticalScaleDown(sheetState),
        ) {
            if (dragHandle != null) {
                val collapseActionLabel = "Collapse action"
                val dismissActionLabel = "Dismiss action"
                val expandActionLabel = "Expand action"

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(
                            interactionSource = null,
                            indication = null,
                        ) {
                            when (sheetState.currentValue) {
                                Expanded -> animateToDismiss()
                                PartiallyExpanded -> scope.launch { sheetState.expand() }
                                else -> scope.launch { sheetState.show() }
                            }
                        }.semantics(mergeDescendants = true) {
                            // Provides semantics to interact with the bottomsheet based on its
                            // current value.
                            if (sheetGesturesEnabled) {
                                with(sheetState) {
                                    dismiss(dismissActionLabel) {
                                        animateToDismiss()
                                        true
                                    }
                                    if (currentValue == PartiallyExpanded) {
                                        expand(expandActionLabel) {
                                            if (anchoredDraggableState.confirmValueChange(
                                                    Expanded,
                                                )
                                            ) {
                                                scope.launch { sheetState.expand() }
                                            }
                                            true
                                        }
                                    } else if (hasPartiallyExpandedState) {
                                        collapse(collapseActionLabel) {
                                            if (anchoredDraggableState.confirmValueChange(
                                                    PartiallyExpanded,
                                                )
                                            ) {
                                                scope.launch { partialExpand() }
                                            }
                                            true
                                        }
                                    }
                                }
                            }
                        },
                ) {
                    dragHandle()
                }
            }
            content()
        }
    }
}

private fun GraphicsLayerScope.calculatePredictiveBackScaleX(progress: Float): Float {
    val width = size.width
    return if (width.isNaN() || width == 0f) {
        1f
    } else {
        1f - lerp(0f, min(PredictiveBackMaxScaleXDistance.toPx(), width), progress) / width
    }
}

private fun GraphicsLayerScope.calculatePredictiveBackScaleY(progress: Float): Float {
    val height = size.height
    return if (height.isNaN() || height == 0f) {
        1f
    } else {
        1f - lerp(0f, min(PredictiveBackMaxScaleYDistance.toPx(), height), progress) / height
    }
}

@Composable
internal expect fun ModalBottomSheetDialog(
    onDismissRequest: () -> Unit,
    properties: ModalBottomSheetProperties,
    predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    content: @Composable () -> Unit,
)

@Immutable
public expect class ModalBottomSheetProperties(
    shouldDismissOnBackPress: Boolean = true,
) {
    public val shouldDismissOnBackPress: Boolean
}

/** Default values for [app.sigot.core.ui.components.ModalBottomSheet] */
@Immutable
public expect object ModalBottomSheetDefaults {
    /** Properties used to customize the behavior of a [app.sigot.core.ui.components.ModalBottomSheet]. */
    public val properties: ModalBottomSheetProperties
}

/**
 * Create and [remember] a [SheetState] for [BasicModalBottomSheet].
 *
 * @param skipPartiallyExpanded Whether the partially expanded state, if the sheet is tall enough,
 *   should be skipped. If true, the sheet will always expand to the [Expanded] state and move to
 *   the [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
public fun rememberModalBottomSheetState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValue) -> Boolean = { true },
): SheetState =
    rememberSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = confirmValueChange,
        initialValue = Hidden,
    )

@Composable
private fun Scrim(
    color: Color,
    onDismissRequest: () -> Unit,
    visible: Boolean,
) {
    // TODO Load the motionScheme tokens from the component tokens file
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = MotionSchemeKeyTokens.DefaultEffect.value(), // TODO: COMEBACK
        )
        val closeSheet = "Close sheet"
        val dismissSheet = if (visible) {
            Modifier
                .pointerInput(onDismissRequest) { detectTapGestures { onDismissRequest() } }
                .semantics(mergeDescendants = true) {
                    traversalIndex = 1f
                    contentDescription = closeSheet
                    onClick {
                        onDismissRequest()
                        true
                    }
                }
        } else {
            Modifier
        }
        Canvas(
            Modifier.fillMaxSize().then(dismissSheet),
        ) {
            drawRect(color = color, alpha = alpha.coerceIn(0f, 1f))
        }
    }
}

/**
 * A [Modifier] that scales up the drawing layer on the Y axis in case the [SheetState]'s
 * anchoredDraggableState offset overflows below the min anchor coordinates. The scaling will ensure
 * that there is no visible gap between the sheet and the edge of the screen in case the sheet
 * bounces when it opens due to a more expressive motion setting.
 *
 * A [verticalScaleDown] should be applied to the content of the sheet to maintain the content
 * aspect ratio as the container scales up.
 *
 * @param state a [SheetState]
 * @see verticalScaleDown
 */
internal fun Modifier.verticalScaleUp(state: SheetState) =
    graphicsLayer {
        val offset = state.anchoredDraggableState.offset
        val anchor = state.anchoredDraggableState.anchors.minAnchor()
        val overflow = if (offset < anchor) anchor - offset else 0f
        scaleY = if (overflow > 0f) (size.height + overflow) / size.height else 1f
        transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0f)
    }

/**
 * A [Modifier] that scales down the drawing layer on the Y axis in case the [SheetState]'s
 * anchoredDraggableState offset overflows below the min anchor coordinates. This modifier should be
 * applied to the content inside a component that was scaled up with a [verticalScaleUp] modifier.
 * It will ensure that the content maintains its aspect ratio as the container scales up.
 *
 * @param state a [SheetState]
 * @see verticalScaleUp
 */
internal fun Modifier.verticalScaleDown(state: SheetState) =
    graphicsLayer {
        val offset = state.anchoredDraggableState.offset
        val anchor = state.anchoredDraggableState.anchors.minAnchor()
        val overflow = if (offset < anchor) anchor - offset else 0f
        scaleY = if (overflow > 0f) 1 / ((size.height + overflow) / size.height) else 1f
        transformOrigin = TransformOrigin(pivotFractionX = 0.5f, pivotFractionY = 0f)
    }

private val PredictiveBackMaxScaleXDistance = 48.dp
private val PredictiveBackMaxScaleYDistance = 24.dp
private val PredictiveBackChildTransformOrigin = TransformOrigin(0.5f, 0f)
