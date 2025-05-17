package app.sigot.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme

@Composable
public fun BrutalContainer(
    shape: Shape,
    elevation: Dp,
    modifier: Modifier = Modifier,
    color: Color = BrutalDefaults.Color,
    extraY: Boolean = false,
    border: Boolean = false,
    content: @Composable () -> Unit,
) {
    val yOffset by animateDpAsState(if (elevation == 0.dp) 0.dp else elevation + 2.dp)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = elevation, y = if (extraY) yOffset else elevation)
                .background(color, shape = shape),
        )

        if (border) {
            Box(
                modifier = Modifier.border(
                    width = BrutalDefaults.BorderWidth,
                    color = BrutalDefaults.Color,
                    shape = shape,
                ),
            ) {
                content()
            }
        } else {
            content()
        }
    }
}

@Composable
public fun Modifier.brutalBorder(
    color: Color = BrutalDefaults.Color,
    width: Dp = BrutalDefaults.BorderWidth * 2,
    shape: Shape = RectangleShape,
): Modifier =
    border(
        width = BrutalDefaults.BorderWidth,
        color = color,
        shape = shape,
    )

@Suppress("ConstPropertyName")
public object BrutalDefaults {
    public const val DisabledAlpha: Float = 0.8f
    public val BorderWidth: Dp = 2.dp
    public val Color: Color
        @Composable get() = AppTheme.colors.outline
}

@Immutable
public data class BrutalElevation(
    public val default: Dp,
    public val pressed: Dp,
    public val focused: Dp,
    public val hovered: Dp,
    public val dragged: Dp,
    public val disabled: Dp,
)

public object BrutalElevationDefaults {
    public val ExtraSmall: BrutalElevation = BrutalElevation(
        default = 1.dp,
        pressed = 0.5.dp,
        focused = 0.5.dp,
        hovered = 0.5.dp,
        dragged = 0.5.dp,
        disabled = 0.dp,
    )

    public val Small: BrutalElevation = BrutalElevation(
        default = 2.dp,
        pressed = 1.dp,
        focused = 1.dp,
        hovered = 1.dp,
        dragged = 1.dp,
        disabled = 0.dp,
    )

    public val Medium: BrutalElevation = BrutalElevation(
        default = 4.dp,
        pressed = 2.dp,
        focused = 2.dp,
        hovered = 2.dp,
        dragged = 2.dp,
        disabled = 0.dp,
    )

    public val Large: BrutalElevation = BrutalElevation(
        default = 8.dp,
        pressed = 4.dp,
        focused = 4.dp,
        hovered = 4.dp,
        dragged = 4.dp,
        disabled = 0.dp,
    )
}
