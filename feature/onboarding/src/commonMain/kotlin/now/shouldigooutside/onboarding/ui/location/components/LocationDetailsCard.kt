package now.shouldigooutside.onboarding.ui.location.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.location.LocationResult
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.location_details
import now.shouldigooutside.core.resources.location_get
import now.shouldigooutside.core.resources.location_latitude
import now.shouldigooutside.core.resources.location_longitude
import now.shouldigooutside.core.resources.location_place
import now.shouldigooutside.core.resources.location_status
import now.shouldigooutside.core.resources.location_status_error
import now.shouldigooutside.core.resources.location_status_idle
import now.shouldigooutside.core.resources.location_status_tracking
import now.shouldigooutside.core.resources.location_status_update
import now.shouldigooutside.core.resources.not_applicable
import now.shouldigooutside.core.resources.try_again
import now.shouldigooutside.core.ui.AppTheme
import now.shouldigooutside.core.ui.BrutalColors
import now.shouldigooutside.core.ui.brutal
import now.shouldigooutside.core.ui.components.Button
import now.shouldigooutside.core.ui.components.ButtonVariant
import now.shouldigooutside.core.ui.components.Icon
import now.shouldigooutside.core.ui.components.Text
import now.shouldigooutside.core.ui.components.progressindicators.LinearProgressIndicator
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Check
import now.shouldigooutside.core.ui.icons.lucide.MapPin
import now.shouldigooutside.core.ui.icons.lucide.MapPinCheckInside
import now.shouldigooutside.core.ui.icons.lucide.MapPinXInside
import now.shouldigooutside.core.ui.icons.lucide.MapPinned
import now.shouldigooutside.core.ui.icons.lucide.Search
import now.shouldigooutside.core.ui.ktx.get
import now.shouldigooutside.core.ui.preview.AppPreview
import now.shouldigooutside.core.ui.preview.PreviewData
import org.jetbrains.compose.resources.StringResource

@Composable
internal fun LocationDetailsCard(
    location: Location?,
    isLoading: Boolean,
    status: LocationResult?,
    modifier: Modifier = Modifier,
    getLocation: () -> Unit = {},
) {
    val colors = brutalColors(status, isLoading)
    LocationCard(
        colors = colors,
        modifier = modifier,
    ) {
        Header(
            icon = rememberIcon(status, isLoading),
            text = rememberText(status, isLoading),
            indicatorColor = colors.bright,
        )

        Column(
            modifier = Modifier.padding(top = 16.dp),
        ) {
            if (status is LocationResult.Failed) {
                ErrorContent(onClick = getLocation)
            } else if (isLoading) {
                LoadingContent()
            } else if (location == null) {
                ReadyContent(onClick = getLocation)
            } else {
                LocationDetails(location = location, onClick = getLocation)
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        color = AppTheme.colors.brutal.blue.bright,
        trackColor = AppTheme.colors.brutal.blue.lowest,
        modifier = modifier,
    )
}

@Composable
private fun ReadyContent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Button(
            text = Res.string.location_get.get(),
            onClick = onClick,
            variant = ButtonVariant.SecondaryElevated,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ErrorContent(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Button(
            text = Res.string.try_again.get(),
            onClick = onClick,
            variant = ButtonVariant.DestructiveElevated,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LocationDetails(
    location: Location?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon = AppIcons.Lucide.MapPinned)

            Column {
                Text(
                    text = Res.string.location_details,
                    style = AppTheme.typography.h3,
                )

                Column(modifier = Modifier.padding(top = 8.dp, end = 4.dp)) {
                    LocationValueRow(
                        label = Res.string.location_latitude,
                        value = location?.roundedLatitude,
                    )

                    LocationValueRow(
                        label = Res.string.location_longitude,
                        value = location?.roundedLongitude,
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            text = "Refresh",
            onClick = onClick,
            variant = ButtonVariant.SecondaryElevated,
            modifier = Modifier.fillMaxWidth(),
        )
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
            text = "${label.get()}: ${value ?: Res.string.not_applicable.get()}",
            modifier = Modifier.weight(1f),
        )

        Icon(icon = AppIcons.Lucide.Check)
    }
}

@Composable
private fun rememberIcon(
    result: LocationResult?,
    isLoading: Boolean,
): ImageVector =
    remember(result, isLoading) {
        when {
            isLoading -> AppIcons.Lucide.Search
            else -> when (result) {
                is LocationResult.Failed -> AppIcons.Lucide.MapPinXInside
                is LocationResult.Success -> AppIcons.Lucide.MapPinCheckInside
                null -> AppIcons.Lucide.MapPin
            }
        }
    }

@Composable
private fun rememberText(
    result: LocationResult?,
    isLoading: Boolean,
): String {
    val res = remember(result, isLoading) {
        when {
            isLoading -> Res.string.location_status_tracking
            else -> when (result) {
                is LocationResult.Failed -> Res.string.location_status_error
                is LocationResult.Success -> Res.string.location_status_update
                null -> Res.string.location_status_idle
            }
        }
    }.get()

    return Res.string.location_status.get(res)
}

@Composable
private fun brutalColors(
    result: LocationResult?,
    isLoading: Boolean,
): BrutalColors =
    when {
        isLoading -> AppTheme.colors.brutal.blue
        else -> when (result) {
            is LocationResult.Failed -> AppTheme.colors.brutal.red
            is LocationResult.Success -> AppTheme.colors.brutal.green
            null -> AppTheme.colors.brutal.pink
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
                isLoading = false,
                status = null,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text("Tracking", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = null,
                isLoading = true,
                status = null,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text("Error", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = null,
                isLoading = false,
                status = LocationResult.Error(),
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Text("Success", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = PreviewData.fakeLocation,
                isLoading = false,
                status = LocationResult.Success(PreviewData.fakeLocation),
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
                isLoading = false,
                status = LocationResult.Success(PreviewData.fakeLocation),
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text("Location", style = AppTheme.typography.h3)
            LocationDetailsCard(
                location = PreviewData.fakeLocation,
                isLoading = false,
                status = LocationResult.Success(PreviewData.fakeLocation),
            )
        }
    }
}
