package now.shouldigooutside.core.ui.components

import androidx.annotation.IntRange
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.lighten
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.LocalContentColor
import now.shouldigooutside.core.ui.LocalThemeIsDark
import now.shouldigooutside.core.ui.foundation.slider.BasicRangeSlider
import now.shouldigooutside.core.ui.foundation.slider.BasicSlider
import now.shouldigooutside.core.ui.foundation.slider.RangeSliderState
import now.shouldigooutside.core.ui.foundation.slider.SliderColors
import now.shouldigooutside.core.ui.foundation.slider.SliderFoundationDefaults
import now.shouldigooutside.core.ui.foundation.slider.SliderFoundationDefaults.ThumbHeight
import now.shouldigooutside.core.ui.foundation.slider.SliderFoundationDefaults.ThumbSizeOnPress
import now.shouldigooutside.core.ui.foundation.slider.SliderFoundationDefaults.ThumbWidth
import now.shouldigooutside.core.ui.foundation.slider.SliderState
import now.shouldigooutside.core.ui.ktx.disabled
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
public fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    shape: Shape = SliderDefaults.Shape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    @IntRange(from = 0) steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    tickLabel: (@Composable (Float) -> Unit)? = null,
) {
    val state =
        remember(steps, valueRange) {
            SliderState(
                value = value,
                steps = steps,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
            )
        }

    state.onValueChangeFinished = onValueChangeFinished
    state.onValueChange = onValueChange
    state.value = value

    Slider(
        state = state,
        modifier = modifier,
        enabled = enabled,
        shape = shape,
        interactionSource = interactionSource,
        colors = colors,
        tickLabel = tickLabel,
    )
}

private val thumbSize = 28.dp

@Composable
private fun TickLabelsRow(
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    tickLabel: @Composable (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tickCount = steps + 2
    val tickValues = List(tickCount) { index ->
        valueRange.start + (valueRange.endInclusive - valueRange.start) * index / (tickCount - 1)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ThumbWidth / 2),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        tickValues.forEachIndexed { index, value ->
            val alignment = when (index) {
                0 -> Alignment.CenterStart
                tickValues.lastIndex -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = alignment,
            ) {
                tickLabel(value)
            }
        }
    }
}

@Composable
public fun Slider(
    state: SliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(),
    shape: Shape = SliderDefaults.Shape,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    tickLabel: (@Composable (Float) -> Unit)? = null,
) {
    require(state.steps >= 0) { "steps should be >= 0" }

    Column(modifier = modifier) {
        BasicSlider(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            colors = colors,
            enabled = enabled,
            trackInsideCornerSize = 1.dp,
            trackHeight = 32.dp,
            thumbWidth = ThumbWidth,
            thumbHeight = ThumbHeight,
            interactionSource = interactionSource,
            thumb = {
                SliderFoundationDefaults.Thumb(
                    interactionSource = interactionSource,
                    colors = colors,
                    enabled = enabled,
                    thumbWidth = ThumbWidth,
                    thumbHeight = ThumbHeight,
                    thumbSizeOnPress = ThumbSizeOnPress,
                    modifier = Modifier.border(
                        width = BrutalDefaults.BorderWidth,
                        color = BrutalDefaults.Color,
                        shape = SliderFoundationDefaults.ThumbShape,
                    ),
                )
            },
            track = { sliderState ->
                BrutalContainer(
                    shape = shape,
                    elevation = SliderDefaults.Elevation,
                ) {
                    SliderFoundationDefaults.Track(
                        colors = colors,
                        enabled = enabled,
                        sliderState = sliderState,
                        modifier = Modifier.border(
                            width = BrutalDefaults.BorderWidth,
                            color = BrutalDefaults.Color,
                            shape = shape,
                        ),
                    )
                }
            },
        )

        if (tickLabel != null) {
            TickLabelsRow(
                valueRange = state.valueRange,
                steps = state.steps,
                tickLabel = tickLabel,
            )
        }
    }
}

@Composable
public fun RangeSlider(
    value: ClosedFloatingPointRange<Float>,
    onValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
    shape: Shape = SliderDefaults.Shape,
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    tickLabel: (@Composable (Float) -> Unit)? = null,
) {
    val state =
        remember(steps, valueRange) {
            RangeSliderState(
                activeRangeStart = value.start,
                activeRangeEnd = value.endInclusive,
                steps = steps,
                onValueChangeFinished = onValueChangeFinished,
                valueRange = valueRange,
            )
        }

    state.onValueChangeFinished = onValueChangeFinished
    state.onValueChange = { onValueChange(it.start..it.endInclusive) }
    state.activeRangeStart = value.start
    state.activeRangeEnd = value.endInclusive

    RangeSlider(
        state = state,
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        shape = shape,
        startInteractionSource = startInteractionSource,
        endInteractionSource = endInteractionSource,
        tickLabel = tickLabel,
    )
}

@Composable
public fun RangeSlider(
    state: RangeSliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(),
    shape: Shape = SliderDefaults.Shape,
    startInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    endInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    tickLabel: (@Composable (Float) -> Unit)? = null,
) {
    require(state.steps >= 0) { "steps should be >= 0" }

    @Composable
    fun Thumb(interactionSource: MutableInteractionSource) {
        SliderFoundationDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
            thumbWidth = SliderFoundationDefaults.ThumbWidth,
            thumbHeight = SliderFoundationDefaults.ThumbHeight,
            thumbSizeOnPress = SliderFoundationDefaults.ThumbSizeOnPress,
            modifier = Modifier.border(
                width = BrutalDefaults.BorderWidth,
                color = BrutalDefaults.Color,
                shape = SliderFoundationDefaults.ThumbShape,
            ),
        )
    }

    Column(modifier = modifier) {
        BasicRangeSlider(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            enabled = enabled,
            startInteractionSource = startInteractionSource,
            endInteractionSource = endInteractionSource,
            colors = colors,
            startThumb = { Thumb(startInteractionSource) },
            endThumb = { Thumb(endInteractionSource) },
            track = { rangeSliderState ->
                BrutalContainer(
                    shape = shape,
                    elevation = SliderDefaults.Elevation,
                ) {
                    SliderFoundationDefaults.Track(
                        rangeSliderState = rangeSliderState,
                        colors = colors,
                        enabled = enabled,
                        modifier = Modifier.border(
                            width = BrutalDefaults.BorderWidth,
                            color = BrutalDefaults.Color,
                            shape = shape,
                        ),
                    )
                }
            },
        )

        if (tickLabel != null) {
            TickLabelsRow(
                valueRange = state.valueRange,
                steps = state.steps,
                tickLabel = tickLabel,
            )
        }
    }
}

@Stable
public object SliderDefaults {
    public val Shape: Shape @Composable get() = AppTheme.shapes.small
    public val Elevation: Dp = BrutalElevationDefaults.Small.default

    @Composable
    private fun thumbColor(): Color =
        if (LocalThemeIsDark.current) {
            AppTheme.colors.inverseSurface
        } else {
            AppTheme.colors.surface
        }

    @Composable
    private fun disabledInactiveTrackColor(): Color =
        if (LocalThemeIsDark.current) {
            AppTheme.colors.surface.lighten(0.8f)
        } else {
            AppTheme.colors.disabled.darken(2f)
        }

    @Composable
    public fun Thumb(
        colors: SliderColors,
        enabled: Boolean,
        interactionSource: MutableInteractionSource,
    ) {
        SliderFoundationDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
            thumbWidth = thumbSize,
            thumbHeight = thumbSize,
            thumbSizeOnPress = DpSize(thumbSize, thumbSize),
            modifier = Modifier.border(
                width = BrutalDefaults.BorderWidth,
                color = BrutalDefaults.Color,
                shape = SliderFoundationDefaults.ThumbShape,
            ),
        )
    }

    @Composable
    public fun TickLabel(value: Float) {
        Text(
            text = value.toInt().toString(),
            style = AppTheme.typography.label3,
            color = LocalContentColor.current,
        )
    }

    @Composable
    public fun colors(
        thumbColor: Color = thumbColor(),
        activeTrackColor: Color = AppTheme.colors.primary,
        activeTickColor: Color = AppTheme.colors.onPrimary,
        inactiveTrackColor: Color = AppTheme.colors.secondary,
        inactiveTickColor: Color = AppTheme.colors.onPrimary.disabled(),
        disabledThumbColor: Color = thumbColor(),
        disabledActiveTrackColor: Color = AppTheme.colors.disabled,
        disabledActiveTickColor: Color = AppTheme.colors.disabled,
        disabledInactiveTrackColor: Color = disabledInactiveTrackColor(),
        disabledInactiveTickColor: Color = Color.Unspecified,
    ): SliderColors =
        SliderColors(
            thumbColor = thumbColor,
            activeTrackColor = activeTrackColor,
            activeTickColor = activeTickColor,
            inactiveTrackColor = inactiveTrackColor,
            inactiveTickColor = inactiveTickColor,
            disabledThumbColor = disabledThumbColor,
            disabledActiveTrackColor = disabledActiveTrackColor,
            disabledActiveTickColor = disabledActiveTickColor,
            disabledInactiveTrackColor = disabledInactiveTrackColor,
            disabledInactiveTickColor = disabledInactiveTickColor,
        )
}

@Composable
private fun SliderPreview() {
    Column(
        modifier =
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Text(
            text = "Slider Components",
            style = AppTheme.typography.h3,
        )

        Column {
            Text(
                text = "Basic Slider",
                style = AppTheme.typography.h4,
            )
            var value by remember { mutableFloatStateOf(0.5f) }
            Slider(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Stepped Slider (5 steps)",
                style = AppTheme.typography.h4,
            )
            var value by remember { mutableFloatStateOf(0.4f) }
            Slider(
                value = value,
                onValueChange = { value = it },
                steps = 4,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Custom Range (0-100)",
                style = AppTheme.typography.h4,
            )
            var value by remember { mutableFloatStateOf(30f) }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${value.toInt()}",
                    style = AppTheme.typography.body1,
                    modifier = Modifier.width(40.dp),
                )
            }
        }

        Column {
            Text(
                text = "Tick Labels (Default)",
                style = AppTheme.typography.h4,
            )
            var value by remember { mutableFloatStateOf(50f) }
            Slider(
                value = value,
                onValueChange = { value = it },
                valueRange = 0f..100f,
                steps = 3,
                tickLabel = { SliderDefaults.TickLabel(it) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Tick Labels (Custom)",
                style = AppTheme.typography.h4,
            )
            var value by remember { mutableFloatStateOf(20f) }
            Slider(
                value = value,
                onValueChange = { value = it },
                valueRange = -10f..40f,
                steps = 1,
                tickLabel = { v ->
                    Text(
                        text = "${v.toInt()}°C",
                        style = AppTheme.typography.label3,
                        color = AppTheme.colors.onSurface,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Disabled States",
                style = AppTheme.typography.h4,
            )
            Slider(
                value = 0.3f,
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Slider(
                value = 0.7f,
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Interactive Slider",
                style = AppTheme.typography.h4,
            )
            var value by remember { mutableFloatStateOf(50f) }
            var isEditing by remember { mutableStateOf(false) }
            Text(
                text = if (isEditing) "Editing..." else "Value: ${value.toInt()}",
                style = AppTheme.typography.body1,
            )
            Slider(
                value = value,
                onValueChange = {
                    value = it
                    isEditing = true
                },
                valueRange = 0f..100f,
                onValueChangeFinished = { isEditing = false },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
internal fun SliderLightPreview() {
    AppPreview { SliderPreview() }
}

@Preview
@Composable
internal fun SliderDarkPreview() {
    AppPreview(isDarkTheme = true) { SliderPreview() }
}

@Composable
private fun RangeSliderPreview() {
    Column(
        modifier =
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        Text(
            text = "Range Slider Components",
            style = AppTheme.typography.h3,
        )

        Column {
            Text(
                text = "Basic Range Slider",
                style = AppTheme.typography.h4,
            )
            var range by remember { mutableStateOf(0.2f..0.8f) }
            RangeSlider(
                value = range,
                onValueChange = { range = it },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Stepped Range Slider (5 steps)",
                style = AppTheme.typography.h4,
            )
            var range by remember { mutableStateOf(0.2f..0.6f) }
            RangeSlider(
                value = range,
                onValueChange = { range = it },
                steps = 4,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Custom Range (0-100)",
                style = AppTheme.typography.h4,
            )
            var range by remember { mutableStateOf(20f..80f) }
            Column {
                RangeSlider(
                    value = range,
                    onValueChange = { range = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Start: ${range.start.toInt()}",
                        style = AppTheme.typography.body1,
                    )
                    Text(
                        text = "End: ${range.endInclusive.toInt()}",
                        style = AppTheme.typography.body1,
                    )
                }
            }
        }

        Column {
            Text(
                text = "Tick Labels (Default)",
                style = AppTheme.typography.h4,
            )
            var range by remember { mutableStateOf(20f..80f) }
            RangeSlider(
                value = range,
                onValueChange = { range = it },
                valueRange = 0f..100f,
                steps = 3,
                tickLabel = { SliderDefaults.TickLabel(it) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Tick Labels (Custom)",
                style = AppTheme.typography.h4,
            )
            var range by remember { mutableStateOf(-5f..30f) }
            RangeSlider(
                value = range,
                onValueChange = { range = it },
                valueRange = -10f..40f,
                steps = 1,
                tickLabel = { v ->
                    Text(
                        text = "${v.toInt()}°C",
                        style = AppTheme.typography.label3,
                        color = AppTheme.colors.onSurface,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Disabled State",
                style = AppTheme.typography.h4,
            )
            RangeSlider(
                value = 0.3f..0.7f,
                onValueChange = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column {
            Text(
                text = "Interactive Range Slider",
                style = AppTheme.typography.h4,
            )
            var range by remember { mutableStateOf(30f..70f) }
            var isEditing by remember { mutableStateOf(false) }
            Text(
                text = if (isEditing) {
                    "Editing..."
                } else {
                    "Range: ${range.start.toInt()} - ${range.endInclusive.toInt()}"
                },
                style = AppTheme.typography.body1,
            )
            RangeSlider(
                value = range,
                onValueChange = {
                    range = it
                    isEditing = true
                },
                valueRange = 0f..100f,
                onValueChangeFinished = { isEditing = false },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
internal fun RangeSliderLightPreview() {
    AppPreview { RangeSliderPreview() }
}

@Preview
@Composable
internal fun RangeSliderDarkPreview() {
    AppPreview(isDarkTheme = true) { RangeSliderPreview() }
}
