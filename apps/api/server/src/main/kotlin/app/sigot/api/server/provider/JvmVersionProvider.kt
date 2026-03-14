package app.sigot.api.server.provider

import app.sigot.core.domain.VersionProvider
import app.sigot.core.model.Version

internal class JvmVersionProvider : VersionProvider {
    override fun provide(): Version =
        Version(
            code = app.sigot.core.Version.CODE,
            name = app.sigot.core.Version.NAME,
            sha = app.sigot.core.Version.GIT_SHA,
        )
}
