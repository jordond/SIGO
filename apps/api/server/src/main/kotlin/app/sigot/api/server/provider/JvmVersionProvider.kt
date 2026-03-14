package app.sigot.api.server.provider

import app.sigot.core.Version.CODE
import app.sigot.core.Version.GIT_SHA
import app.sigot.core.Version.NAME
import app.sigot.core.domain.VersionProvider
import app.sigot.core.model.Version

internal class JvmVersionProvider : VersionProvider {
    override fun provide(): Version =
        Version(
            code = CODE,
            name = NAME,
            sha = GIT_SHA,
        )
}
