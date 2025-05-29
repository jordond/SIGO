package app.sigot.cli.config

import app.sigot.core.domain.forecast.ApiTokenProvider

class CliApiTokenProvider : ApiTokenProvider {
    var token: String = ""

    override fun provide(): String = token.takeIf { it.isNotEmpty() } ?: error("Token not set")
}
