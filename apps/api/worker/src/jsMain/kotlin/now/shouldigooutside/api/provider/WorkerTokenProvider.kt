package now.shouldigooutside.api.provider

import now.shouldigooutside.core.domain.forecast.ApiTokenProvider

/**
 * Per-request [ApiTokenProvider] constructed from the Cloudflare Worker `env`
 * binding. Immutable; one instance lives only for the duration of a single
 * `fetch` invocation via the Koin RequestScope.
 */
internal class WorkerTokenProvider(
    private val token: String,
) : ApiTokenProvider {
    override fun provide(): String = token
}
