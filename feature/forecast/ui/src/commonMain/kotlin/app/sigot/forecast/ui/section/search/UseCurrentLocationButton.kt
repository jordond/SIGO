package app.sigot.forecast.ui.section.search

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
import app.sigot.core.resources.Res
import app.sigot.core.resources.location_use_current
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.components.Button
import app.sigot.core.ui.components.ButtonVariant
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.icons.lucide.Locate
import app.sigot.core.ui.icons.lucide.LocateFixed
import app.sigot.core.ui.icons.lucide.Lucide
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview

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
