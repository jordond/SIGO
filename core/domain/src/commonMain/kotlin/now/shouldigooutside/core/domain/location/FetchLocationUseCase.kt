package now.shouldigooutside.core.domain.location

import now.shouldigooutside.core.model.location.LocationResult

public interface FetchLocationUseCase {
    public suspend fun fetch(force: Boolean = false): LocationResult
}
