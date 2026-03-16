package now.shouldigooutside.core.ui.preferences

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.units.PrecipitationUnit
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.cardColors
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Switch
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.card.ElevatedCard
import now.shouldigooutside.core.ui.ktx.clickableWithoutRipple
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.mappers.units.colors
import now.shouldigooutside.core.ui.switchColors
import org.jetbrains.compose.resources.StringResource

@Composable
public fun PrecipitationToggle(
    text: StringResource,
    icon: ImageVector,
    checked: Boolean,
    update: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = PrecipitationUnit.colors()
    ElevatedCard(
        colors = colors.cardColors(),
        modifier = Modifier.clickableWithoutRipple(onClick = { update(!checked) }),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
        ) {
            Icon(
                icon = icon,
                contentDescription = text.get(),
            )
            Text(
                text = text,
                style = AppTheme.typography.h4,
            )
            Spacer(Modifier.width(16.dp))
            Switch(
                checked = checked,
                onCheckedChange = update,
                colors = colors.switchColors(),
            )
        }
    }
}
