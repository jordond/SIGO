package now.shouldigooutside.api.provider

import now.shouldigooutside.core.domain.forecast.ApiTokenProvider

class WorkerTokenProvider : ApiTokenProvider {
    var apiToken: String? = null

    override fun provide(): String = apiToken ?: error("Api token is not set")
}
