package app.sigot.onboarding.ui.location.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.sigot.core.resources.Res
import app.sigot.core.resources.location_geolocation_supported
import app.sigot.core.resources.location_geolocation_unavailable
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.brutal
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.MapPin
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview

@Composable
internal fun LocationServicesCard(
    locationServicesAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = if (locationServicesAvailable) {
        AppTheme.colors.brutal.green
    } else {
        AppTheme.colors.brutal.red
    }

    LocationCard(
        colors = colors,
        modifier = modifier,
    ) {
        val text = remember(locationServicesAvailable) {
            if (locationServicesAvailable) {
                Res.string.location_geolocation_supported
            } else {
                Res.string.location_geolocation_unavailable
            }
        }

        Header(
            icon = AppIcons.Lucide.MapPin,
            text = text.get(),
            indicatorColor = colors.bright,
        )
    }
}

@Preview
@Composable
private fun LocationServicesCardPreview() {
    AppPreview {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            LocationServicesCard(locationServicesAvailable = true)
            LocationServicesCard(locationServicesAvailable = false)
        }
    }
}
