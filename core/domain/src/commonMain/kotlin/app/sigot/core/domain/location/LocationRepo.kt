package app.sigot.core.domain.location

import app.sigot.core.model.location.LocationPermissionStatus
import app.sigot.core.model.location.LocationResult

public interface LocationRepo {
    public fun hasPermission(): Boolean

    public suspend fun requestPermission(): LocationPermissionStatus

    public suspend fun location(): LocationResult
}
