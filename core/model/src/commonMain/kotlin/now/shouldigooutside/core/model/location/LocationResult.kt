package now.shouldigooutside.core.model.location

import androidx.compose.runtime.Immutable

@Immutable
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

    public class NotFound : Failed()

    public class NotSupported : Failed()

    public class Error : Failed()
}
