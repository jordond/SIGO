package app.sigot.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.LocalContainerColor
import app.sigot.core.ui.LocalContentColor
import app.sigot.core.ui.LocalThemeIsDark
import app.sigot.core.ui.components.SwitchDefaults.RippleRadius
import app.sigot.core.ui.components.SwitchDefaults.SwitchHeight
import app.sigot.core.ui.components.SwitchDefaults.SwitchWidth
import app.sigot.core.ui.components.SwitchDefaults.ThumbBorderWidth
import app.sigot.core.ui.components.SwitchDefaults.ThumbSize
import app.sigot.core.ui.components.SwitchDefaults.ThumbSizeStateOffset
import app.sigot.core.ui.components.SwitchDefaults.TrackBorderWidth
import app.sigot.core.ui.components.SwitchDefaults.TrackShape
import app.sigot.core.ui.components.SwitchDefaults.UncheckedThumbSize
import app.sigot.core.ui.contentColorFor
import app.sigot.core.ui.foundation.ripple
import app.sigot.core.ui.preview.AppPreview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@Composable
public fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    thumbContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val scope = rememberCoroutineScope()
    val pressed by interactionSource.collectIsPressedAsState()

    val animationState =
        remember {
            SwitchAnimationState(checked, pressed)
        }

    LaunchedEffect(checked, pressed) {
        animationState.animateTo(checked, pressed, scope)
    }

    val toggleableModifier =
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null,
            )
        } else {
            Modifier
        }

    BrutalContainer(
        shape = TrackShape,
        elevation = BrutalElevationDefaults.Small.default,
    ) {
        SwitchComponent(
            modifier = modifier.then(toggleableModifier),
            checked = checked,
            enabled = enabled,
            colors = colors,
            interactionSource = interactionSource,
            thumbContent = thumbContent,
            thumbPosition = animationState.thumbPosition.value,
            thumbSizeOffset = animationState.thumbSizeOffset.value,
        )
    }
}

@Composable
private fun SwitchComponent(
    modifier: Modifier,
    checked: Boolean,
    enabled: Boolean,
    colors: SwitchColors,
    interactionSource: InteractionSource,
    thumbContent: (@Composable () -> Unit)?,
    thumbPosition: Float,
    thumbSizeOffset: Float,
) {
    val borderColor = remember(colors, enabled, checked) {
        colors.borderColor(enabled = enabled, checked = checked)
    }

    Box(
        modifier =
            modifier
                .size(SwitchWidth, SwitchHeight)
                .background(
                    color = remember(enabled, checked) { colors.trackColor(enabled, checked) },
                    shape = TrackShape,
                ).border(
                    width = TrackBorderWidth,
                    color = borderColor,
                    shape = TrackShape,
                ),
    ) {
        val checkedThumbSize = remember(thumbPosition) {
            UncheckedThumbSize + ThumbSizeStateOffset * thumbPosition
        }

        val uncheckedThumbSize = remember(thumbPosition, thumbSizeOffset, thumbPosition) {
            UncheckedThumbSize +
                ThumbSizeStateOffset * if (thumbPosition == 0f) thumbSizeOffset else thumbPosition
        }

        val thumbSize = if (checked) checkedThumbSize else uncheckedThumbSize
        val verticalPadding = (SwitchHeight - ThumbSize) / 1.5f

        Box(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .size(thumbSize)
                    .offset {
                        val trackWidth = SwitchWidth.toPx()
                        val currentThumbSize = thumbSize.toPx()
                        val maxThumbSize = ThumbSize.toPx()
                        val padding = verticalPadding.toPx()

                        val totalMovableDistance = trackWidth - maxThumbSize - (padding * 2)
                        val sizeDifference = (maxThumbSize - currentThumbSize) / 2

                        IntOffset(
                            x = (padding + sizeDifference + (totalMovableDistance * thumbPosition))
                                .roundToInt(),
                            y = 0,
                        )
                    }.drawBehind {
                        val borderWidth = ThumbBorderWidth.toPx()
                        drawCircle(
                            color = colors.thumbBorderColor(enabled, checked),
                        )
                        drawCircle(
                            color = colors.thumbColor(enabled, checked),
                            radius = size.minDimension / 2 - borderWidth,
                        )
                    }.indication(
                        interactionSource = interactionSource,
                        indication =
                            ripple(
                                bounded = true,
                                radius = RippleRadius,
                            ),
                    ),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbContent != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.iconColor(enabled, checked),
                ) {
                    thumbContent()
                }
            }
        }
    }
}

public object SwitchDefaults {
    public val ThumbSize: Dp = 16.dp
    public val UncheckedThumbSize: Dp = 16.dp
    public val ThumbSizeStateOffset: Dp = ThumbSize - UncheckedThumbSize
    public val SwitchWidth: Dp = 48.dp
    public val SwitchHeight: Dp = 24.dp
    public val TrackBorderWidth: Dp = 2.dp
    public val ThumbBorderWidth: Dp = TrackBorderWidth
    public val TrackShape: Shape @Composable get() = AppTheme.shapes.medium
    public val RippleRadius: Dp = 20.dp

    @Composable
    private fun thumbColor(): Color =
        if (LocalThemeIsDark.current) {
            AppTheme.colors.inverseSurface
        } else {
            AppTheme.colors.surface
        }

    @Composable
    public fun colors(
        checkedThumbColor: Color = thumbColor(),
        checkedTrackColor: Color = AppTheme.colors.primary,
        checkedBorderColor: Color = BrutalDefaults.Color,
        checkedIconColor: Color = contentColorFor(checkedThumbColor),
        checkedThumbBorderColor: Color = BrutalDefaults.Color,
        uncheckedThumbColor: Color = thumbColor(),
        uncheckedTrackColor: Color = LocalContainerColor.current,
        uncheckedBorderColor: Color = BrutalDefaults.Color,
        uncheckedIconColor: Color = contentColorFor(uncheckedThumbColor),
        uncheckedThumbBorderColor: Color = BrutalDefaults.Color,
        disabledCheckedThumbColor: Color = thumbColor(),
        disabledCheckedTrackColor: Color = AppTheme.colors.disabled,
        disabledCheckedBorderColor: Color = BrutalDefaults.Color,
        disabledCheckedIconColor: Color = contentColorFor(disabledCheckedThumbColor),
        disabledCheckedThumbBorderColor: Color = BrutalDefaults.Color,
        disabledUncheckedThumbColor: Color = thumbColor(),
        disabledUncheckedTrackColor: Color = AppTheme.colors.disabled,
        disabledUncheckedBorderColor: Color = BrutalDefaults.Color,
        disabledUncheckedIconColor: Color = contentColorFor(disabledUncheckedThumbColor),
        disabledUncheckedThumbBorderColor: Color = BrutalDefaults.Color,
    ): SwitchColors =
        SwitchColors(
            checkedThumbColor = checkedThumbColor,
            checkedTrackColor = checkedTrackColor,
            checkedBorderColor = checkedBorderColor,
            checkedIconColor = checkedIconColor,
            checkedThumbBorderColor = checkedThumbBorderColor,
            uncheckedThumbColor = uncheckedThumbColor,
            uncheckedTrackColor = uncheckedTrackColor,
            uncheckedBorderColor = uncheckedBorderColor,
            uncheckedIconColor = uncheckedIconColor,
            uncheckedThumbBorderColor = uncheckedThumbBorderColor,
            disabledCheckedThumbColor = disabledCheckedThumbColor,
            disabledCheckedTrackColor = disabledCheckedTrackColor,
            disabledCheckedBorderColor = disabledCheckedBorderColor,
            disabledCheckedIconColor = disabledCheckedIconColor,
            disabledCheckedThumbBorderColor = disabledCheckedThumbBorderColor,
            disabledUncheckedThumbColor = disabledUncheckedThumbColor,
            disabledUncheckedTrackColor = disabledUncheckedTrackColor,
            disabledUncheckedBorderColor = disabledUncheckedBorderColor,
            disabledUncheckedIconColor = disabledUncheckedIconColor,
            disabledUncheckedThumbBorderColor = disabledUncheckedThumbBorderColor,
        )

    @Composable
    public fun primaryColors(): SwitchColors = colors(checkedTrackColor = AppTheme.colors.primary)

    @Composable
    public fun secondaryColors(): SwitchColors = colors(checkedTrackColor = AppTheme.colors.secondary)

    @Composable
    public fun tertiaryColors(): SwitchColors = colors(checkedTrackColor = AppTheme.colors.tertiary)
}

@Stable
public class SwitchColors(
    private val checkedThumbColor: Color,
    private val checkedTrackColor: Color,
    private val checkedBorderColor: Color,
    private val checkedIconColor: Color,
    private val checkedThumbBorderColor: Color,
    private val uncheckedThumbColor: Color,
    private val uncheckedTrackColor: Color,
    private val uncheckedBorderColor: Color,
    private val uncheckedIconColor: Color,
    private val uncheckedThumbBorderColor: Color,
    private val disabledCheckedThumbColor: Color,
    private val disabledCheckedTrackColor: Color,
    private val disabledCheckedBorderColor: Color,
    private val disabledCheckedIconColor: Color,
    private val disabledCheckedThumbBorderColor: Color,
    private val disabledUncheckedThumbColor: Color,
    private val disabledUncheckedTrackColor: Color,
    private val disabledUncheckedBorderColor: Color,
    private val disabledUncheckedIconColor: Color,
    private val disabledUncheckedThumbBorderColor: Color,
) {
    @Stable
    internal fun thumbColor(
        enabled: Boolean,
        checked: Boolean,
    ): Color =
        when {
            enabled && checked -> checkedThumbColor
            enabled && !checked -> uncheckedThumbColor
            !enabled && checked -> disabledCheckedThumbColor
            else -> disabledUncheckedThumbColor
        }

    @Stable
    internal fun trackColor(
        enabled: Boolean,
        checked: Boolean,
    ): Color =
        when {
            enabled && checked -> checkedTrackColor
            enabled && !checked -> uncheckedTrackColor
            !enabled && checked -> disabledCheckedTrackColor
            else -> disabledUncheckedTrackColor
        }

    @Stable
    internal fun borderColor(
        enabled: Boolean,
        checked: Boolean,
    ): Color =
        when {
            enabled && checked -> checkedBorderColor
            enabled && !checked -> uncheckedBorderColor
            !enabled && checked -> disabledCheckedBorderColor
            else -> disabledUncheckedBorderColor
        }

    @Stable
    internal fun thumbBorderColor(
        enabled: Boolean,
        checked: Boolean,
    ): Color =
        when {
            enabled && checked -> checkedThumbBorderColor
            enabled && !checked -> uncheckedThumbBorderColor
            !enabled && checked -> disabledCheckedThumbBorderColor
            else -> disabledUncheckedThumbBorderColor
        }

    @Stable
    internal fun iconColor(
        enabled: Boolean,
        checked: Boolean,
    ): Color =
        when {
            enabled && checked -> checkedIconColor
            enabled && !checked -> uncheckedIconColor
            !enabled && checked -> disabledCheckedIconColor
            else -> disabledUncheckedIconColor
        }
}

@Stable
private class SwitchAnimationState(
    initialChecked: Boolean,
    initialPressed: Boolean,
) {
    var checked by mutableStateOf(initialChecked)
    var pressed by mutableStateOf(initialPressed)

    val thumbPosition = Animatable(if (checked) 1f else 0f)
    val thumbSizeOffset = Animatable(0f)

    val animationSpec =
        tween<Float>(
            durationMillis = 100,
            easing = FastOutSlowInEasing,
        )

    fun animateTo(
        targetChecked: Boolean,
        targetPressed: Boolean,
        scope: CoroutineScope,
    ) {
        checked = targetChecked
        pressed = targetPressed

        scope.launch {
            thumbPosition.animateTo(
                targetValue = if (targetChecked) 1f else 0f,
                animationSpec = animationSpec,
            )
        }
        scope.launch {
            thumbSizeOffset.animateTo(
                targetValue = if (targetPressed) 1f else 0f,
                animationSpec = animationSpec,
            )
        }
    }
}

@Composable
private fun SwitchPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp),
    ) {
        val value =
            remember {
                mutableStateOf(false)
            }

        Switch(
            checked = value.value,
            onCheckedChange = { value.value = it },
        )

        Switch(
            checked = true,
            onCheckedChange = { value.value = it },
        )

        Switch(
            checked = false,
            onCheckedChange = { value.value = it },
        )

        Switch(
            checked = true,
            enabled = false,
            onCheckedChange = { value.value = it },
        )

        Switch(
            checked = false,
            enabled = false,
            onCheckedChange = { value.value = it },
        )
    }
}

@Preview
@Composable
internal fun SwitchLightPreview() {
    AppPreview {
        SwitchPreview()
    }
}

@Preview
@Composable
internal fun SwitchDarkPreview() {
    AppPreview(isDarkTheme = true) {
        SwitchPreview()
    }
}
