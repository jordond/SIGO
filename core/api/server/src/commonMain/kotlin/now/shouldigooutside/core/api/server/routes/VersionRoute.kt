package now.shouldigooutside.core.api.server.routes

import now.shouldigooutside.core.api.model.ApiRoutePath
import now.shouldigooutside.core.api.model.entity.VersionResponse
import now.shouldigooutside.core.api.model.entity.toEntity
import now.shouldigooutside.core.api.server.ApiRoute
import now.shouldigooutside.core.api.server.http.ServerRequest
import now.shouldigooutside.core.api.server.http.ServerResponse
import now.shouldigooutside.core.api.server.util.cached
import now.shouldigooutside.core.api.server.util.ok
import now.shouldigooutside.core.domain.VersionProvider
import kotlin.time.Duration.Companion.hours

public class VersionRoute(
    private val versionProvider: VersionProvider,
) : ApiRoute {
    override val path: ApiRoutePath = ApiRoutePath.Version

    override suspend fun get(
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        val version = versionProvider.provide().toEntity()
        val response = VersionResponse(version)
        return cached(24.hours) {
            ok(response)
        }
    }
}
