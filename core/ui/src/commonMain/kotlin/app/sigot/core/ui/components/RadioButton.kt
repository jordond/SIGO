package app.sigot.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.components.RadioButtonDefaults.MinimumInteractiveSize
import app.sigot.core.ui.components.RadioButtonDefaults.RadioAnimationDuration
import app.sigot.core.ui.components.RadioButtonDefaults.RadioButtonIconSize
import app.sigot.core.ui.components.RadioButtonDefaults.RadioButtonPadding
import app.sigot.core.ui.components.RadioButtonDefaults.RadioSelectedStrokeWidth
import app.sigot.core.ui.components.RadioButtonDefaults.RadioStrokeWidth
import app.sigot.core.ui.contentColorFor
import app.sigot.core.ui.foundation.ripple
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
public fun RadioButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable (() -> Unit)? = null,
) {
    val strokeWidth =
        animateDpAsState(
            targetValue = if (selected) RadioSelectedStrokeWidth else RadioStrokeWidth,
            animationSpec = tween(durationMillis = RadioAnimationDuration),
            label = "RadioButtonStrokeWidth",
        )
    val radioColor = colors.radioColor(enabled, selected)
    val selectableModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication =
                    ripple(
                        bounded = false,
                        radius = MinimumInteractiveSize / 2,
                    ),
            )
        } else {
            Modifier
        }

    val clickableModifier =
        if (onClick != null && content != null) {
            Modifier.clickable(
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
        } else {
            Modifier
        }

    Row(
        modifier = clickableModifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Canvas(
            modifier
                .then(
                    if (onClick != null) {
                        Modifier.requiredSize(MinimumInteractiveSize)
                    } else {
                        Modifier
                    },
                ).then(selectableModifier)
                .wrapContentSize(Alignment.Center)
                .padding(RadioButtonPadding)
                .requiredSize(RadioButtonIconSize),
        ) {
            drawCircle(
                color = radioColor.value,
                radius = (RadioButtonIconSize / 2).toPx() - strokeWidth.value.toPx() / 2,
                style = Stroke(strokeWidth.value.toPx()),
            )
        }
        if (content != null) {
            content()
        }
    }
}

@Suppress("ConstPropertyName")
public object RadioButtonDefaults {
    public const val RadioAnimationDuration: Int = 100

    public val RadioButtonPadding: Dp = 2.dp
    public val RadioSelectedStrokeWidth: Dp = 6.dp
    public val RadioStrokeWidth: Dp = 2.dp
    public val RadioButtonIconSize: Dp = 20.dp
    public val MinimumInteractiveSize: Dp = 44.dp

    @Composable
    public fun colors(
        selectedColor: Color = contentColorFor(LocalContainerColor.current),
        unselectedColor: Color = contentColorFor(LocalContainerColor.current),
        disabledSelectedColor: Color = AppTheme.colors.disabled,
        disabledUnselectedColor: Color = AppTheme.colors.disabled,
    ): RadioButtonColors =
        RadioButtonColors(
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
            disabledSelectedColor = disabledSelectedColor,
            disabledUnselectedColor = disabledUnselectedColor,
        )
}

@Immutable
public data class RadioButtonColors(
    val selectedColor: Color,
    val unselectedColor: Color,
    val disabledSelectedColor: Color,
    val disabledUnselectedColor: Color,
) {
    @Composable
    internal fun radioColor(
        enabled: Boolean,
        selected: Boolean,
    ): State<Color> {
        val target =
            when {
                enabled && selected -> selectedColor
                enabled && !selected -> unselectedColor
                !enabled && selected -> disabledSelectedColor
                else -> disabledUnselectedColor
            }

        return if (enabled) {
            animateColorAsState(target, tween(durationMillis = RadioAnimationDuration), label = "radioColor")
        } else {
            rememberUpdatedState(target)
        }
    }
}

@Composable
private fun RadioButtonPreview() {
    var selected by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(32.dp)) {
        RadioButton(selected = false, onClick = {}, content = { Text("Unselected") })
        RadioButton(selected = true, onClick = {}, content = { Text("Selected") })
        RadioButton(selected = selected, onClick = { selected = !selected }, content = { Text("Toggle") })
        RadioButton(selected = false, enabled = false, onClick = {}, content = { Text("Disabled") })
        RadioButton(selected = true, enabled = false, onClick = {}, content = { Text("Disabled Checked") })
    }
}

@Preview
@Composable
internal fun RadioButtonLightPreview() {
    AppPreview { RadioButtonPreview() }
}

@Preview
@Composable
internal fun RadioButtonDarkPreview() {
    AppPreview(isDarkTheme = true) { RadioButtonPreview() }
}
