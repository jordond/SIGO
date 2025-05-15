package app.sigot.cli.config

import app.sigot.core.domain.forecast.VisualCrossingTokenProvider

class ApiTokenProvider : VisualCrossingTokenProvider {
    var token: String = ""

    override fun provide(): String = token.takeIf { it.isNotEmpty() } ?: error("Token not set")
}
