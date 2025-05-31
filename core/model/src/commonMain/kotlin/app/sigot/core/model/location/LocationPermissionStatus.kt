package app.sigot.core.model.location

public sealed interface LocationPermissionStatus {
    public data object Unknown : LocationPermissionStatus

    public data object Granted : LocationPermissionStatus

    public data class Denied(
        val permanently: Boolean,
    ) : LocationPermissionStatus
}
