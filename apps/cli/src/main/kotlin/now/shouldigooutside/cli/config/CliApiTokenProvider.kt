package now.shouldigooutside.cli.config

import now.shouldigooutside.core.domain.forecast.ApiTokenProvider

class CliApiTokenProvider : ApiTokenProvider {
    var token: String = ""

    override fun provide(): String = token.takeIf { it.isNotEmpty() } ?: error("Token not set")
}
