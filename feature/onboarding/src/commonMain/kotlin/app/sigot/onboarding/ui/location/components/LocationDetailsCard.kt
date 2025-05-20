package app.sigot.onboarding.ui.location.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.sigot.core.model.location.Location
import app.sigot.core.resources.Res
import app.sigot.core.resources.location_details
import app.sigot.core.resources.location_latitude
import app.sigot.core.resources.location_longitude
import app.sigot.core.resources.location_place
import app.sigot.core.ui.AppTheme
import app.sigot.core.ui.colors
import app.sigot.core.ui.components.Icon
import app.sigot.core.ui.components.Text
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Check
import app.sigot.core.ui.icons.lucide.MapPinned
import app.sigot.core.ui.ktx.get
import app.sigot.core.ui.preview.AppPreview
import app.sigot.core.ui.preview.PreviewData
import app.sigot.core.ui.rememberIcon
import app.sigot.core.ui.rememberText
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.TrackingStatus
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun LocationDetailsCard(
    location: Location?,
    status: TrackingStatus,
    modifier: Modifier = Modifier,
) {
    val colors = status.colors()
    LocationCard(
        colors = colors,
        modifier = modifier,
    ) {
        Header(
            icon = status.rememberIcon(),
            text = status.rememberText(),
            indicatorColor = colors.bright,
        )

        AnimatedVisibility(visible = location != null) {
            LocationDetails(location)
        }
    }
}

@Composable
private fun LocationDetails(
    location: Location?,
    modifier: Modifier = Modifier,
) {
    val padding by animateDpAsState(if (location == null) 0.dp else 16.dp)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = padding),
    ) {
        Icon(icon = AppIcons.Lucide.MapPinned)

        Column {
            Text(
                text = Res.string.location_details,
                style = AppTheme.typography.h3,
            )

            Column(
                modifier = Modifier.padding(top = 8.dp, end = 4.dp),
            ) {
                LocationValueRow(
                    label = Res.string.location_latitude,
                    value = location?.latitude.toString(),
                )

                LocationValueRow(
                    label = Res.string.location_longitude,
                    value = location?.longitude.toString(),
                )

                AnimatedVisibility(visible = location?.isDefaultName == false) {
                    LocationValueRow(
                        label = Res.string.location_place,
                        value = location?.name,
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationValueRow(
    label: StringResource,
    value: String?,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${label.get()}: ${value ?: "N/A"}",
            modifier = Modifier.weight(1f),
        )

        Icon(icon = AppIcons.Lucide.Check)
    }
}

@Preview
@Composable
private fun LocationCardPreview() {
    AppPreview {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Idle", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = null,
                status = TrackingStatus.Idle,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text("Tracking", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = null,
                status = TrackingStatus.Tracking,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text("Error", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = null,
                status = TrackingStatus.Error(GeolocatorResult.GeolocationFailed("Error")),
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text("Success", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = Location(
                    latitude = 12.345,
                    longitude = 67.89,
                    name = "London ON",
                ),
                status = TrackingStatus.Update(PreviewData.compassLocation),
            )
        }
    }
}

@Preview
@Composable
private fun LocationCardWithLocationPreview() {
    AppPreview {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Coords only", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = Location(
                    latitude = 12.345,
                    longitude = 67.89,
                ),
                status = TrackingStatus.Update(PreviewData.compassLocation),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text("Location", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = Location(
                    latitude = 12.345,
                    longitude = 67.89,
                    name = "London ON",
                ),
                status = TrackingStatus.Update(PreviewData.compassLocation),
            )
        }
    }
}
