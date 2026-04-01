package now.shouldigooutside.forecast.ui.activities.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.activities_add_button
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.autoSize
import now.shouldigooutside.core.ui.components.card.CardDefaults
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Plus
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun AddActivityCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        ElevatedCard(
            onClick = onClick,
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.background),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = AppTheme.spacing.mini,
                    alignment = Alignment.CenterHorizontally,
                ),
                modifier = Modifier
                    .padding(
                        vertical = AppTheme.spacing.standard,
                        horizontal = AppTheme.spacing.standard,
                    ).fillMaxWidth(),
            ) {
                Text(
                    text = Res.string.activities_add_button,
                    style = AppTheme.typography.h3,
                    maxLines = 1,
                    autoSize = AppTheme.typography.h3.autoSize(),
                )

                Icon(
                    icon = AppIcons.Lucide.Plus,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Composable
private fun Preview() {
    AppPreview {
        Box(Modifier.padding(16.dp)) {
            AddActivityCard(onClick = {})
        }
    }
}
