package app.sigot.api

import app.sigot.core.domain.forecast.ApiTokenProvider

class WorkerTokenProvider : ApiTokenProvider {
    var apiToken: String? = null

    override fun provide(): String = apiToken ?: error("Api token is not set")
}
