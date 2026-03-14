package app.sigot.api.server.provider

import app.sigot.core.domain.VersionProvider
import app.sigot.core.model.Version

internal class JvmVersionProvider : VersionProvider {
    override fun provide(): Version =
        Version(
            code = 1,
            name = "1.0.0-jvm",
            sha = "dev",
        )
}
