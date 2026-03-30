package now.shouldigooutside.core.ui.components.card

import androidx.compose.animation.animateColorAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.contentColorFor

@Composable
public fun SelectionCard(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = AppTheme.colors.primary,
    unselectedColor: Color = AppTheme.colors.surface,
    content: @Composable () -> Unit,
) {
    val contentColor by animateColorAsState(
        targetValue = AppTheme.colors.contentColorFor(if (isSelected) selectedColor else unselectedColor),
        label = "SelectionCardContentColor",
    )
    ElevatedCard(
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isSelected) selectedColor else unselectedColor,
            contentColor = contentColor,
        ),
        modifier = modifier,
    ) {
        content()
    }
}
