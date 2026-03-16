package now.shouldigooutside.core.domain.location

import now.shouldigooutside.core.model.location.Location

public interface SearchLocationUseCase {
    public suspend fun search(query: String): List<Location>
}
