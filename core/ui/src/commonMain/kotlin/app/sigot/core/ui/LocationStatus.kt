package app.sigot.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.resources.Res
import app.sigot.core.resources.location_status
import app.sigot.core.resources.location_status_error
import app.sigot.core.resources.location_status_idle
import app.sigot.core.resources.location_status_tracking
import app.sigot.core.resources.location_status_update
import app.sigot.core.resources.permission_denied
import app.sigot.core.resources.permission_granted
import app.sigot.core.resources.permission_status
import app.sigot.core.resources.permission_unknown
import app.sigot.core.ui.icons.AppIcons
import app.sigot.core.ui.icons.lucide.Locate
import app.sigot.core.ui.icons.lucide.LocateFixed
import app.sigot.core.ui.icons.lucide.LocateOff
import app.sigot.core.ui.icons.lucide.MapPin
import app.sigot.core.ui.icons.lucide.MapPinCheckInside
import app.sigot.core.ui.icons.lucide.MapPinXInside
import app.sigot.core.ui.icons.lucide.Search
import app.sigot.core.ui.ktx.get
import dev.jordond.compass.geolocation.TrackingStatus

@Composable
public fun LocationPermissionStatus.rememberIcon(): ImageVector =
    remember(this) {
        when (this) {
            is LocationPermissionStatus.Denied -> AppIcons.Lucide.LocateOff
            is LocationPermissionStatus.Granted -> AppIcons.Lucide.LocateFixed
            is LocationPermissionStatus.Unknown -> AppIcons.Lucide.Locate
        }
    }

@Composable
public fun LocationPermissionStatus.colors(): BrutalColors =
    when (this) {
        is LocationPermissionStatus.Denied -> AppTheme.colors.brutal.red
        is LocationPermissionStatus.Granted -> AppTheme.colors.brutal.green
        is LocationPermissionStatus.Unknown -> AppTheme.colors.brutal.yellow
    }

@Composable
public fun LocationPermissionStatus.rememberText(): String {
    val statusText = remember(this) {
        when (this) {
            is LocationPermissionStatus.Denied -> Res.string.permission_denied
            is LocationPermissionStatus.Granted -> Res.string.permission_granted
            is LocationPermissionStatus.Unknown -> Res.string.permission_unknown
        }
    }.get()

    return Res.string.permission_status.get(statusText)
}

@Composable
public fun TrackingStatus.rememberText(): String {
    val res = remember(this) {
        when (this) {
            is TrackingStatus.Error -> Res.string.location_status_error
            is TrackingStatus.Idle -> Res.string.location_status_idle
            is TrackingStatus.Tracking -> Res.string.location_status_tracking
            is TrackingStatus.Update -> Res.string.location_status_update
        }
    }.get()

    return Res.string.location_status.get(res)
}

@Composable
public fun TrackingStatus.indicatorColor(): Color =
    when (this) {
        is TrackingStatus.Error -> AppTheme.colors.brutal.red.bright
        is TrackingStatus.Idle -> AppTheme.colors.brutal.yellow.bright
        is TrackingStatus.Tracking -> AppTheme.colors.brutal.blue.bright
        is TrackingStatus.Update -> AppTheme.colors.brutal.green.bright
    }

@Composable
public fun TrackingStatus.colors(): BrutalColors =
    when (this) {
        is TrackingStatus.Error -> AppTheme.colors.brutal.red
        is TrackingStatus.Idle -> AppTheme.colors.brutal.pink
        is TrackingStatus.Tracking -> AppTheme.colors.brutal.blue
        is TrackingStatus.Update -> AppTheme.colors.brutal.green
    }

@Composable
public fun TrackingStatus.rememberIcon(): ImageVector =
    remember(this) {
        when (this) {
            is TrackingStatus.Error -> AppIcons.Lucide.MapPinXInside
            is TrackingStatus.Idle -> AppIcons.Lucide.MapPin
            is TrackingStatus.Tracking -> AppIcons.Lucide.Search
            is TrackingStatus.Update -> AppIcons.Lucide.MapPinCheckInside
        }
    }
