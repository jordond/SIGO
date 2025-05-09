package app.sigot.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
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
    offset: Dp,
    modifier: Modifier = Modifier,
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

@Suppress("ConstPropertyName")
public object BrutalDefaults {
    public const val DisabledAlpha: Float = 0.8f
    public val BorderWidth: Dp = 2.dp
    public val Color: Color
        @Composable get() = AppTheme.colors.outline
}

public object BrutalElevationDefaults {
    public object Small {
        public val defaultElevation: Dp = 2.dp
        public val pressedElevation: Dp = 1.dp
        public val focusedElevation: Dp = 1.dp
        public val hoveredElevation: Dp = 1.dp
        public val draggedElevation: Dp = 1.dp
        public val disabledElevation: Dp = 0.dp
    }

    public object Medium {
        public val defaultElevation: Dp = 4.dp
        public val pressedElevation: Dp = 2.dp
        public val focusedElevation: Dp = 2.dp
        public val hoveredElevation: Dp = 2.dp
        public val draggedElevation: Dp = 2.dp
        public val disabledElevation: Dp = 0.dp
    }

    public object Large {
        public val defaultElevation: Dp = 8.dp
        public val pressedElevation: Dp = 4.dp
        public val focusedElevation: Dp = 4.dp
        public val hoveredElevation: Dp = 4.dp
        public val draggedElevation: Dp = 4.dp
        public val disabledElevation: Dp = 0.dp
    }
}
