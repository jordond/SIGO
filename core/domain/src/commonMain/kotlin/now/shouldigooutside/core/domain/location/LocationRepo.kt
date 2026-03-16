package now.shouldigooutside.core.domain.location

import now.shouldigooutside.core.model.location.LocationPermissionStatus
import now.shouldigooutside.core.model.location.LocationResult

public interface LocationRepo {
    public fun hasPermission(): Boolean

    public suspend fun requestPermission(): LocationPermissionStatus

    public suspend fun location(): LocationResult
}
