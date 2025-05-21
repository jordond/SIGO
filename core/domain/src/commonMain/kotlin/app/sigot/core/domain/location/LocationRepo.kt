package app.sigot.core.domain.location

import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus

public interface LocationRepo {
    public fun hasPermission(): Boolean

    public suspend fun requestPermission(): LocationPermissionStatus

    public suspend fun location(resolve: Boolean = false): LocationResult
}

public sealed interface LocationResult {
    public sealed interface Failed : LocationResult

    public data class Success(
        val location: Location,
    ) : LocationResult

    public data class NotAllowed(
        val permanent: Boolean,
    ) : Failed

    public data object NotSupported : Failed

    public data object Error : Failed
}
