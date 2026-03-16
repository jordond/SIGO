package now.shouldigooutside.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import now.shouldigooutside.core.model.location.LocationPermissionStatus
import now.shouldigooutside.core.resources.Res
import now.shouldigooutside.core.resources.permission_denied
import now.shouldigooutside.core.resources.permission_granted
import now.shouldigooutside.core.resources.permission_status
import now.shouldigooutside.core.resources.permission_unknown
import now.shouldigooutside.core.ui.icons.AppIcons
import now.shouldigooutside.core.ui.icons.lucide.Locate
import now.shouldigooutside.core.ui.icons.lucide.LocateFixed
import now.shouldigooutside.core.ui.icons.lucide.LocateOff
import now.shouldigooutside.core.ui.ktx.get

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
        is LocationPermissionStatus.Unknown -> AppTheme.colors.brutal.pink
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
