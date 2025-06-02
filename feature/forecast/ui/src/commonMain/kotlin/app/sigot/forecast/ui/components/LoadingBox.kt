package app.sigot.forecast.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import app.sigot.core.resources.Res
import app.sigot.core.resources.loading_ellipses
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.brutal
import app.sigot.core.ui.components.HorizontalDivider
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.components.brutalBorder
import app.sigot.core.ui.components.card.CardDefaults
import app.sigot.core.ui.components.card.ElevatedCard
import app.sigot.core.ui.components.progressindicators.LinearProgressIndicator
import app.sigot.core.ui.components.progressindicators.ProgressTextPosition
import app.sigot.core.ui.preview.AppPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun LoadingBox(
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

    ElevatedCard(
        modifier = modifier.widthIn(max = 400.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = AppTheme.colors.brutal.pink.lowest,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.tertiary)
                    .padding(AppTheme.spacing.small),
            ) {
                Box(
                    modifier = Modifier
                        .background(AppTheme.colors.tertiary, CircleShape)
                        .size(20.dp)
                        .brutalBorder(shape = CircleShape),
                )

                Box(
                    modifier = Modifier
                        .background(AppTheme.colors.primary, CircleShape)
                        .size(20.dp)
                        .brutalBorder(shape = CircleShape),
                )

                Box(
                    modifier = Modifier
                        .background(AppTheme.colors.secondary, CircleShape)
                        .size(20.dp)
                        .brutalBorder(shape = CircleShape),
                )
            }

            HorizontalDivider()

            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
                modifier = Modifier
                    .padding(horizontal = AppTheme.spacing.large, vertical = AppTheme.spacing.standard)
                    .fillMaxWidth(),
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
