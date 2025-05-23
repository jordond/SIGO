package app.sigot.core.model.location

public sealed interface LocationResult {
    public sealed class Failed :
        Throwable(),
        LocationResult

    public data class Success(
        val location: Location,
    ) : LocationResult

    public data class NotAllowed(
        val permanent: Boolean,
    ) : Failed()

    public data object NotFound : Failed()

    public data object NotSupported : Failed()

    public data object Error : Failed()
}
