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
    elevation: Dp,
    modifier: Modifier = Modifier,
    color: Color = BrutalDefaults.Color,
    extraY: Boolean = false,
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
    public object ExtraSmall {
        public val default: Dp = 1.dp
        public val pressed: Dp = 0.5.dp
        public val focused: Dp = 0.5.dp
        public val hovered: Dp = 0.5.dp
        public val dragged: Dp = 0.5.dp
        public val disabled: Dp = 0.dp
    }

    public object Small {
        public val default: Dp = 2.dp
        public val pressed: Dp = 1.dp
        public val focused: Dp = 1.dp
        public val hovered: Dp = 1.dp
        public val dragged: Dp = 1.dp
        public val disabled: Dp = 0.dp
    }

    public object Medium {
        public val default: Dp = 4.dp
        public val pressed: Dp = 2.dp
        public val focused: Dp = 2.dp
        public val hovered: Dp = 2.dp
        public val dragged: Dp = 2.dp
        public val disabled: Dp = 0.dp
    }

    public object Large {
        public val default: Dp = 8.dp
        public val pressed: Dp = 4.dp
        public val focused: Dp = 4.dp
        public val hovered: Dp = 4.dp
        public val dragged: Dp = 4.dp
        public val disabled: Dp = 0.dp
    }
}
