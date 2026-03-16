package now.shouldigooutside.core.api.server.cors

import now.shouldigooutside.core.api.server.http.ServerRequest
import now.shouldigooutside.core.api.server.http.ServerResponse

public interface CorsHandler {
    public fun validateOrigin(request: ServerRequest): ServerResponse?

    public fun preflight(request: ServerRequest): ServerResponse

    public fun withCorsHeaders(
        response: ServerResponse,
        request: ServerRequest,
    ): ServerResponse
}
