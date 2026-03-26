package now.shouldigooutside.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
public fun RetroBox(
    modifier: Modifier = Modifier,
    colors: BrutalColors = AppTheme.colors.brutal.pink,
    enabled: Boolean = true,
    maxWidth: Dp = 400.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    ElevatedCard(
        modifier = modifier.widthIn(max = maxWidth),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colors.lowest,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.high)
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

            Box(
                modifier = Modifier
                    .padding(AppTheme.spacing.standard)
                    .fillMaxWidth(),
            ) {
                content()
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        RetroBox(
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "Howdy",
                style = AppTheme.typography.h3,
            )
        }
    }
}
