package now.shouldigooutside.core.api.server

import now.shouldigooutside.core.api.server.http.ServerRequest
import now.shouldigooutside.core.api.server.http.ServerResponse

public interface ApiRouter {
    public suspend fun handle(request: ServerRequest): ServerResponse
}
