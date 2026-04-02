package now.shouldigooutside.di

import now.shouldigooutside.core.domain.VersionProvider
import now.shouldigooutside.core.model.Version

internal class AppVersionProvider : VersionProvider {
    override fun provide(): Version {
        val v = now.shouldigooutside.core.Version
        return Version(
            code = v.CODE,
            name = v.NAME,
            sha = v.GIT_SHA,
        )
    }
}
