package app.sigot.core.domain.location

import app.sigot.core.model.location.Location
import app.sigot.core.model.location.LocationPermissionStatus
import dev.jordond.compass.geolocation.TrackingStatus
import kotlinx.coroutines.flow.Flow

public interface LocationRepo {
    public fun hasPermission(): Boolean

    public suspend fun requestPermission(): LocationPermissionStatus

    public suspend fun location(resolve: Boolean = false): LocationResult

    public fun track(): Flow<TrackingStatus>
}

public sealed interface LocationResult {
    public data class Success(
        val location: Location,
    ) : LocationResult

    public data class NotAllowed(
        val permanent: Boolean,
    ) : LocationResult

    public data object NotSupported : LocationResult

    public data object Error : LocationResult
}
