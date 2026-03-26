package now.shouldigooutside.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.loading_ellipses
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.progressindicators.LinearProgressIndicator
import now.shouldigooutside.core.ui.components.progressindicators.ProgressTextPosition
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
public fun LoadingBox(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var targetProgress by remember { mutableStateOf(0f) }

    val progress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing,
            ),
        ),
        label = "loading_progress",
    )

    LaunchedEffect(enabled) {
        targetProgress = (if (enabled) 1f else 0f)
    }

    RetroBox(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
            modifier = Modifier,
        ) {
            Text(
                text = Res.string.loading_ellipses,
                style = AppTheme.typography.h3,
            )

            LinearProgressIndicator(
                progress = { progress },
                textPosition = ProgressTextPosition.None,
                strokeCap = StrokeCap.Square,
                modifier = Modifier.padding(bottom = AppTheme.spacing.small),
            )
        }
    }
}

@Preview
@Composable
private fun LoadingBoxPreview() {
    AppPreview {
        LoadingBox(
            modifier = Modifier.padding(32.dp),
        )
    }
}
