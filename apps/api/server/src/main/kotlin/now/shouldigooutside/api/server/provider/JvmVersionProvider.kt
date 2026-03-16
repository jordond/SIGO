package now.shouldigooutside.api.server.provider

import now.shouldigooutside.core.Version.CODE
import now.shouldigooutside.core.Version.GIT_SHA
import now.shouldigooutside.core.Version.NAME
import now.shouldigooutside.core.domain.VersionProvider
import now.shouldigooutside.core.model.Version

internal class JvmVersionProvider : VersionProvider {
    override fun provide(): Version =
        Version(
            code = CODE,
            name = NAME,
            sha = GIT_SHA,
        )
}
