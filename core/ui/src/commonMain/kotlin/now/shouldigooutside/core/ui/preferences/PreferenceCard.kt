package now.shouldigooutside.core.ui.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.LocalTextStyle
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.HorizontalDivider
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Switch
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.switchColors
import org.jetbrains.compose.resources.StringResource

@Composable
public fun PreferenceCard(
    title: StringResource,
    description: StringResource,
    icon: ImageVector,
    colors: BrutalColors,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (RowScope.() -> Unit)? = null,
    body: @Composable ColumnScope.() -> Unit,
) {
    val headerAlpha by animateFloatAsState(if (enabled) 1f else 0.6f)
    ElevatedCard(
        colors = colors.cardColors(),
        modifier = modifier,
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .background(colors.bright)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .alpha(headerAlpha),
                ) {
                    Text(
                        text = title,
                        style = AppTheme.typography.h3,
                    )

                    Text(
                        text = description,
                        autoSize = LocalTextStyle.current.autoSize(),
                        maxLines = 1,
                    )
                }

                Icon(
                    icon = icon,
                    contentDescription = title.get(),
                    modifier = Modifier.alpha(headerAlpha),
                )

                if (trailing != null) {
                    trailing()
                }

                Switch(
                    checked = enabled,
                    onCheckedChange = onEnabledChange,
                    colors = colors.switchColors(),
                )
            }

            AnimatedVisibility(
                visible = enabled,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    HorizontalDivider()
                    body()
                }
            }
        }
    }
}
