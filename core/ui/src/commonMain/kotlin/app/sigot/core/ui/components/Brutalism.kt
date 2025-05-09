package app.sigot.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.sigot.core.ui.AppTheme

@Composable
public fun BrutalContainer(
    shape: Shape,
    modifier: Modifier = Modifier,
    offset: Dp = BrutalDefaults.Offset,
    color: Color = BrutalDefaults.Color,
    extraY: Boolean = false,
    content: @Composable () -> Unit,
) {
    val yOffset by animateDpAsState(if (offset == 0.dp) 0.dp else offset + 2.dp)
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = offset, y = if (extraY) yOffset else offset)
                .background(color, shape = shape),
        )

        content()
    }
}

public object BrutalDefaults {
    public val Shape: Shape @Composable get() = RoundedCornerShape(12.0.dp)
    public val BorderWidth: Dp = 2.dp
    public val Offset: Dp = BrutalElevationDefaults.defaultElevation
    public val Color: Color
        @Composable get() = AppTheme.colors.outline
}

public object BrutalElevationDefaults {
    public val defaultElevation: Dp = 4.dp
    public val pressedElevation: Dp = 2.dp
    public val focusedElevation: Dp = 2.dp
    public val hoveredElevation: Dp = 2.dp
    public val draggedElevation: Dp = 2.dp
    public val disabledElevation: Dp = 0.dp
}
