package app.sigot.core.domain.location

import app.sigot.core.model.location.LocationResult

public interface FetchLocationUseCase {
    public suspend fun fetch(force: Boolean = false): LocationResult
}
