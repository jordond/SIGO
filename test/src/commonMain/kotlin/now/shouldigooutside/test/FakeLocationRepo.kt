package now.shouldigooutside.test

import now.shouldigooutside.core.domain.location.LocationRepo
import now.shouldigooutside.core.model.location.LocationPermissionStatus
import now.shouldigooutside.core.model.location.LocationResult

public class FakeLocationRepo(
    public var locationResult: LocationResult = LocationResult.Success(testLocation()),
    public var permissionStatus: LocationPermissionStatus = LocationPermissionStatus.Granted,
) : LocationRepo {
    override fun hasPermission(): Boolean = permissionStatus == LocationPermissionStatus.Granted

    override suspend fun requestPermission(): LocationPermissionStatus = permissionStatus

    override suspend fun location(): LocationResult = locationResult
}
