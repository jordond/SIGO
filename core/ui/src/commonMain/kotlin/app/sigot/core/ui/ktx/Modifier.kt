package app.sigot.core.ui.ktx

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.LocalHaptics
import app.sigot.core.ui.wrap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

public fun Modifier.debugBorder(
    color: Color = Color.Red,
    width: Dp = 1.dp,
): Modifier = border(width, color = color)

@Composable
public fun Modifier.previewBorder(
    color: Color = Color.Black,
    width: Dp = 1.dp,
): Modifier =
    apply {
        if (LocalInspectionMode.current) {
            border(width, color)
        }
    }

public fun Modifier.conditional(
    condition: Boolean,
    block: @Composable () -> Modifier,
): Modifier = if (condition) composed { then(block()) } else this

@Composable
public fun <T> Modifier.whenNotNull(
    value: T?,
    block: @Composable (T) -> Modifier,
): Modifier = if (value != null) composed { then(block(value)) } else this

@Composable
public fun Modifier.then(block: Modifier.() -> Modifier): Modifier = then(block(this))

@Composable
public fun Modifier.clickable(
    coroutineScope: CoroutineScope,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: suspend () -> Unit,
): Modifier =
    clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        onClick = { coroutineScope.launch { onClick() } },
    )

public fun Modifier.nullableClip(shape: Shape?): Modifier = if (shape != null) clip(shape) else this

public fun Modifier.hapticClick(onClick: () -> Unit): Modifier =
    composed {
        val haptics = LocalHaptics.current
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = remember {
                {
                    haptics.vibrate(HapticFeedbackType.LongPress)
                    onClick()
                }
            },
        )
    }

public fun Modifier.clickableWithoutRipple(
    onClick: () -> Unit,
    haptics: Boolean? = null,
): Modifier =
    composed(
        factory = {
            val localHaptics = LocalHaptics.current
            val callback = if (haptics ?: localHaptics.enabled) localHaptics.wrap(onClick) else onClick
            this.then(
                Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = callback,
                ),
            )
        },
    )

public fun Modifier.removeTopBaseline(): Modifier =
    layout { measurable, constraints ->
        // Measure the composable
        val placeable = measurable.measure(constraints)

        // Check the composable has a LastBaseline
        check(placeable[LastBaseline] != AlignmentLine.Unspecified)
        val lastBaseline = placeable[LastBaseline]

        val placeableY = placeable.height - lastBaseline
        val height = placeable.height - placeableY

        layout(placeable.width, height) {
            placeable.placeRelative(0, -placeableY)
        }
    }
