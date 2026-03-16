package now.shouldigooutside.api.provider

import now.shouldigooutside.core.domain.VersionProvider
import now.shouldigooutside.core.model.Version

internal class ApiVersionProvider : VersionProvider {
    override fun provide(): Version =
        Version(
            code = now.shouldigooutside.core.Version.CODE,
            name = now.shouldigooutside.core.Version.NAME,
            sha = now.shouldigooutside.core.Version.GIT_SHA,
        )
}
