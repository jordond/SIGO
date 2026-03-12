package app.sigot.core.domain.location

import app.sigot.core.model.location.Location

public interface SearchLocationUseCase {
    public suspend fun search(query: String): List<Location>
}
