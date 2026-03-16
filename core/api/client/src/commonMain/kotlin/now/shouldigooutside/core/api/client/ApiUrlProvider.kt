package now.shouldigooutside.core.api.client

import now.shouldigooutside.core.api.model.ApiRoutePath

public interface ApiUrlProvider {
    public fun provide(): String

    public fun url(path: ApiRoutePath): String {
        val sanitized = if (path.path.startsWith("/")) path.path.drop(1) else path.path
        return "${provide()}/$sanitized"
    }
}
