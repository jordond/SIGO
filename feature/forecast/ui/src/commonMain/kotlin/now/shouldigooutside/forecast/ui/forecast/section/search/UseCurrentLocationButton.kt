package now.shouldigooutside.forecast.ui.forecast.section.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.location_use_current
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.icons.lucide.Locate
import now.shouldigooutside.core.ui.icons.lucide.LocateFixed
import now.shouldigooutside.core.ui.icons.lucide.Lucide
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview

@Composable
internal fun UseCurrentLocationButton(
    usingCurrentLocation: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        variant = if (usingCurrentLocation) ButtonVariant.Primary else ButtonVariant.Secondary,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                icon = if (usingCurrentLocation) Lucide.LocateFixed else Lucide.Locate,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = Res.string.location_use_current.get(),
                style = AppTheme.typography.button,
            )
        }
    }
}

@Preview
@Composable
private fun UseCurrentLocationButtonPreview() {
    AppPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            UseCurrentLocationButton(
                usingCurrentLocation = true,
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
            UseCurrentLocationButton(
                usingCurrentLocation = false,
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
