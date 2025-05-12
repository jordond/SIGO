package app.sigot.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.components.CheckboxDefaults.BoxInDuration
import app.sigot.core.ui.components.CheckboxDefaults.BoxOutDuration
import app.sigot.core.ui.components.CheckboxDefaults.CheckAnimationDuration
import app.sigot.core.ui.components.CheckboxDefaults.CheckboxDefaultPadding
import app.sigot.core.ui.components.CheckboxDefaults.CheckboxSize
import app.sigot.core.ui.components.CheckboxDefaults.MinimumInteractiveSize
import app.sigot.core.ui.components.CheckboxDefaults.RadiusSize
import app.sigot.core.ui.components.CheckboxDefaults.StrokeWidth
import app.sigot.core.ui.foundation.ripple
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.floor
import kotlin.math.max

@Composable
public fun Checkbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    elevation: BrutalElevation? = CheckboxDefaults.elevation,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    TriStateCheckbox(
        state = ToggleableState(checked),
        onClick =
            if (onCheckedChange != null) {
                { onCheckedChange(!checked) }
            } else {
                null
            },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        elevation = elevation,
        interactionSource = interactionSource,
    )
}

@Composable
public fun TriStateCheckbox(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    elevation: BrutalElevation? = CheckboxDefaults.elevation,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val toggleableModifier =
        if (onClick == null) {
            Modifier
        } else {
            Modifier
                .requiredSize(MinimumInteractiveSize)
                .triStateToggleable(
                    state = state,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Checkbox,
                    interactionSource = interactionSource,
                    indication =
                        ripple(
                            bounded = false,
                            radius = MinimumInteractiveSize / 2,
                        ),
                )
        }

    CheckboxComponent(
        enabled = enabled,
        value = state,
        colors = colors,
        elevation = elevation,
        modifier =
            modifier
                .then(toggleableModifier)
                .padding(CheckboxDefaultPadding),
    )
}

@Composable
private fun CheckboxComponent(
    enabled: Boolean,
    value: ToggleableState,
    modifier: Modifier,
    colors: CheckboxColors,
    elevation: BrutalElevation?,
) {
    val transition = updateTransition(value, label = "checkbox")
    val checkDrawFraction =
        transition.animateFloat(
            transitionSpec = {
                when {
                    initialState == ToggleableState.Off -> snap()
                    targetState == ToggleableState.Off -> snap(delayMillis = BoxOutDuration)
                    else -> tween(durationMillis = CheckAnimationDuration)
                }
            },
            label = "checkDrawFraction",
        ) {
            when (it) {
                ToggleableState.On -> 1f
                ToggleableState.Off -> 0f
                ToggleableState.Indeterminate -> 1f
            }
        }

    val checkCenterGravitationShiftFraction =
        transition.animateFloat(
            transitionSpec = {
                when {
                    initialState == ToggleableState.Off -> snap()
                    targetState == ToggleableState.Off -> snap(delayMillis = BoxOutDuration)
                    else -> tween(durationMillis = CheckAnimationDuration)
                }
            },
            label = "checkCenterGravitationShiftFraction",
        ) {
            when (it) {
                ToggleableState.On -> 0f
                ToggleableState.Off -> 0f
                ToggleableState.Indeterminate -> 1f
            }
        }
    val checkCache = remember { CheckDrawingCache() }
    val checkColor = colors.checkmarkColor(enabled, value)
    val boxColor = colors.boxColor(enabled, value)
    val borderColor = colors.borderColor(enabled, value)

    Box(modifier) {
        BrutalContainer(
            shape = CheckboxDefaults.Shape,
            elevation = elevation?.default ?: 0.dp,
        ) {
            Canvas(Modifier.wrapContentSize(Alignment.Center).requiredSize(CheckboxSize)) {
                val strokeWidthPx = floor(StrokeWidth.toPx())
                drawBox(
                    boxColor = boxColor.value,
                    borderColor = borderColor.value,
                    radius = RadiusSize.toPx(),
                    strokeWidth = strokeWidthPx,
                )
                drawCheck(
                    checkColor = checkColor,
                    checkFraction = checkDrawFraction.value,
                    crossCenterGravitation = checkCenterGravitationShiftFraction.value,
                    strokeWidthPx = strokeWidthPx,
                    drawingCache = checkCache,
                )
            }
        }
    }
}

private fun DrawScope.drawBox(
    boxColor: Color,
    borderColor: Color,
    radius: Float,
    strokeWidth: Float,
) {
    val halfStrokeWidth = strokeWidth / 2.0f
    val stroke = Stroke(strokeWidth)
    val checkboxSize = size.width
    if (boxColor == borderColor) {
        drawRoundRect(
            boxColor,
            size = Size(checkboxSize, checkboxSize),
            cornerRadius = CornerRadius(radius),
            style = Fill,
        )
    } else {
        drawRoundRect(
            boxColor,
            topLeft = Offset(strokeWidth, strokeWidth),
            size = Size(checkboxSize - strokeWidth * 2, checkboxSize - strokeWidth * 2),
            cornerRadius = CornerRadius(max(0f, radius - strokeWidth)),
            style = Fill,
        )
        drawRoundRect(
            borderColor,
            topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
            size = Size(checkboxSize - strokeWidth, checkboxSize - strokeWidth),
            cornerRadius = CornerRadius(radius - halfStrokeWidth),
            style = stroke,
        )
    }
}

private fun DrawScope.drawCheck(
    checkColor: Color,
    checkFraction: Float,
    crossCenterGravitation: Float,
    strokeWidthPx: Float,
    drawingCache: CheckDrawingCache,
) {
    val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
    val width = size.width
    val checkCrossX = 0.4f
    val checkCrossY = 0.7f
    val leftX = 0.2f
    val leftY = 0.5f
    val rightX = 0.8f
    val rightY = 0.3f

    val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
    val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
    val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
    val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

    with(drawingCache) {
        checkPath.reset()
        checkPath.moveTo(width * leftX, width * gravitatedLeftY)
        checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
        checkPath.lineTo(width * rightX, width * gravitatedRightY)
        pathMeasure.setPath(checkPath, false)
        pathToDraw.reset()
        pathMeasure.getSegment(
            startDistance = 0f,
            stopDistance = pathMeasure.length * checkFraction,
            destination = pathToDraw,
            startWithMoveTo = true,
        )
    }
    drawPath(drawingCache.pathToDraw, checkColor, style = stroke)
}

@Immutable
private class CheckDrawingCache(
    val checkPath: Path = Path(),
    val pathMeasure: PathMeasure = PathMeasure(),
    val pathToDraw: Path = Path(),
)

@Suppress("ConstPropertyName")
public object CheckboxDefaults {
    public val elevation: BrutalElevation = BrutalElevationDefaults.Small
    internal const val BoxInDuration = 50
    internal const val BoxOutDuration = 100
    internal const val CheckAnimationDuration = 100

    internal val CheckboxDefaultPadding = 2.dp
    internal val CheckboxSize = 20.dp
    internal val StrokeWidth = BrutalDefaults.BorderWidth
    internal val RadiusSize = 4.dp
    internal val Shape = RoundedCornerShape(RadiusSize)
    internal val MinimumInteractiveSize = 44.dp

    @Composable
    public fun colors(
        checkedCheckmarkColor: Color = AppTheme.colors.onPrimary,
        uncheckedCheckmarkColor: Color = Color.Transparent,
        disabledCheckedCheckmarkColor: Color = AppTheme.colors.onDisabled,
        disabledUncheckedCheckmarkColor: Color = Color.Transparent,
        checkedBoxColor: Color = AppTheme.colors.primary,
        uncheckedBoxColor: Color = LocalContainerColor.current,
        disabledCheckedBoxColor: Color = AppTheme.colors.disabled,
        disabledUncheckedBoxColor: Color = AppTheme.colors.disabled,
        disabledIndeterminateBoxColor: Color = AppTheme.colors.disabled,
        checkedBorderColor: Color = Color.Black,
        uncheckedBorderColor: Color = Color.Black,
        disabledBorderColor: Color = Color.Black,
        disabledUncheckedBorderColor: Color = Color.Black,
        disabledIndeterminateBorderColor: Color = Color.Black,
    ): CheckboxColors =
        CheckboxColors(
            checkedCheckmarkColor = checkedCheckmarkColor,
            uncheckedCheckmarkColor = uncheckedCheckmarkColor,
            disabledCheckedCheckmarkColor = disabledCheckedCheckmarkColor,
            disabledUncheckedCheckmarkColor = disabledUncheckedCheckmarkColor,
            checkedBoxColor = checkedBoxColor,
            uncheckedBoxColor = uncheckedBoxColor,
            disabledCheckedBoxColor = disabledCheckedBoxColor,
            disabledUncheckedBoxColor = disabledUncheckedBoxColor,
            disabledIndeterminateBoxColor = disabledIndeterminateBoxColor,
            checkedBorderColor = checkedBorderColor,
            uncheckedBorderColor = uncheckedBorderColor,
            disabledBorderColor = disabledBorderColor,
            disabledUncheckedBorderColor = disabledUncheckedBorderColor,
            disabledIndeterminateBorderColor = disabledIndeterminateBorderColor,
        )
}

@Immutable
public data class CheckboxColors(
    val checkedCheckmarkColor: Color,
    val uncheckedCheckmarkColor: Color,
    val disabledCheckedCheckmarkColor: Color,
    val disabledUncheckedCheckmarkColor: Color,
    val checkedBoxColor: Color,
    val uncheckedBoxColor: Color,
    val disabledCheckedBoxColor: Color,
    val disabledUncheckedBoxColor: Color,
    val disabledIndeterminateBoxColor: Color,
    val checkedBorderColor: Color,
    val uncheckedBorderColor: Color,
    val disabledBorderColor: Color,
    val disabledUncheckedBorderColor: Color,
    val disabledIndeterminateBorderColor: Color,
) {
    @Composable
    internal fun checkmarkColor(
        enabled: Boolean,
        state: ToggleableState,
    ): Color =
        remember(enabled, state) {
            if (enabled) {
                if (state == ToggleableState.Off) {
                    uncheckedCheckmarkColor
                } else {
                    checkedCheckmarkColor
                }
            } else {
                if (state == ToggleableState.Off) {
                    disabledUncheckedCheckmarkColor
                } else {
                    disabledCheckedCheckmarkColor
                }
            }
        }

    @Composable
    internal fun boxColor(
        enabled: Boolean,
        state: ToggleableState,
    ): State<Color> {
        val target =
            if (enabled) {
                when (state) {
                    ToggleableState.On, ToggleableState.Indeterminate -> checkedBoxColor
                    ToggleableState.Off -> uncheckedBoxColor
                }
            } else {
                when (state) {
                    ToggleableState.On -> disabledCheckedBoxColor
                    ToggleableState.Indeterminate -> disabledIndeterminateBoxColor
                    ToggleableState.Off -> disabledUncheckedBoxColor
                }
            }

        return if (enabled) {
            val duration = if (state == ToggleableState.Off) BoxOutDuration else BoxInDuration
            animateColorAsState(target, tween(durationMillis = duration), label = "animate box color")
        } else {
            rememberUpdatedState(target)
        }
    }

    @Composable
    internal fun borderColor(
        enabled: Boolean,
        state: ToggleableState,
    ): State<Color> {
        val target =
            if (enabled) {
                when (state) {
                    ToggleableState.On, ToggleableState.Indeterminate -> checkedBorderColor
                    ToggleableState.Off -> uncheckedBorderColor
                }
            } else {
                when (state) {
                    ToggleableState.Indeterminate -> disabledIndeterminateBorderColor
                    ToggleableState.On -> disabledBorderColor
                    ToggleableState.Off -> disabledUncheckedBorderColor
                }
            }

        return if (enabled) {
            val duration = if (state == ToggleableState.Off) BoxOutDuration else BoxInDuration
            animateColorAsState(target, tween(durationMillis = duration), label = "animate border color")
        } else {
            rememberUpdatedState(target)
        }
    }
}

@Composable
private fun CheckboxPreview() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        var isChecked by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
            )
            Text("Basic")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = {},
            )
            Text("Basic")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Checkbox(
                    checked = true,
                    onCheckedChange = null,
                    enabled = false,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Disabled Checked")
            }

            Column {
                Checkbox(
                    checked = false,
                    onCheckedChange = null,
                    enabled = false,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text("Disabled Unchecked")
            }
        }

        var triState by remember { mutableStateOf(ToggleableState.Off) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TriStateCheckbox(
                state = triState,
                onClick = {
                    triState =
                        when (triState) {
                            ToggleableState.Off -> ToggleableState.Indeterminate
                            ToggleableState.Indeterminate -> ToggleableState.On
                            ToggleableState.On -> ToggleableState.Off
                        }
                },
            )
            Text("Tri-State Checkbox")
        }

        var customColorChecked by remember { mutableStateOf(false) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val customColors =
                CheckboxDefaults.colors(
                    checkedCheckmarkColor = AppTheme.colors.onPrimary,
                    uncheckedCheckmarkColor = AppTheme.colors.transparent,
                    checkedBoxColor = AppTheme.colors.primary,
                    uncheckedBoxColor = AppTheme.colors.transparent,
                    disabledCheckedBoxColor = AppTheme.colors.disabled,
                    disabledUncheckedBoxColor = AppTheme.colors.transparent,
                    disabledIndeterminateBoxColor = AppTheme.colors.primary,
                    checkedBorderColor = AppTheme.colors.primary,
                    uncheckedBorderColor = AppTheme.colors.primary,
                    disabledBorderColor = AppTheme.colors.disabled,
                    disabledUncheckedBorderColor = AppTheme.colors.disabled,
                    disabledIndeterminateBorderColor = AppTheme.colors.disabled,
                )

            Checkbox(
                checked = customColorChecked,
                onCheckedChange = { customColorChecked = it },
                colors = customColors,
            )
            Text("Custom Colors")
        }

        var selectedItems by remember { mutableStateOf(setOf<String>()) }
        val items = listOf("Option 1", "Option 2", "Option 3")

        Column {
            Text("Checkbox Group")
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { item ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Checkbox(
                        checked = selectedItems.contains(item),
                        onCheckedChange = { checked ->
                            selectedItems =
                                if (checked) {
                                    selectedItems + item
                                } else {
                                    selectedItems - item
                                }
                        },
                    )
                    Text(item)
                }
            }
        }
    }
}

@Preview
@Composable
private fun CheckBoxLightPreview() {
    AppPreview {
        CheckboxPreview()
    }
}

@Preview
@Composable
private fun CheckBoxDarkPreview() {
    AppPreview(isDarkTheme = true) {
        CheckboxPreview()
    }
}
