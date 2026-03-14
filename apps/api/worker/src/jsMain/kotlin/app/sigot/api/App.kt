package app.sigot.api

import app.sigot.api.provider.WorkerTokenProvider
import app.sigot.core.api.server.ApiRouter
import app.sigot.core.api.server.attestation.AttestationConfig
import app.sigot.core.api.server.cache.ForecastCacheProvider
import app.sigot.core.api.server.cache.KvForecastCache
import app.sigot.core.api.server.http.toJsResponse
import app.sigot.core.api.server.http.toServerRequest
import app.sigot.core.api.server.util.serverError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.js.Promise

interface App {
    fun handle(
        request: Request,
        env: dynamic,
    ): Promise<Response>
}

@Serializable
data class Env(
    @SerialName("FORECAST_API_KEY")
    val forecastApiKey: String,
    @SerialName("GOOGLE_SERVICE_ACCOUNT_JSON")
    val googleServiceAccountJson: String? = null,
    @SerialName("APPLE_APP_ID")
    val appleAppId: String? = null,
)

@OptIn(ExperimentalSerializationApi::class)
class DefaultApp(
    scope: CoroutineScope,
    private val router: ApiRouter,
    private val tokenProvider: WorkerTokenProvider,
    private val cacheProvider: ForecastCacheProvider,
    private val attestationConfig: AttestationConfig,
    private val json: Json,
) : App,
    CoroutineScope by scope {
    override fun handle(
        request: Request,
        env: dynamic,
    ): Promise<Response> {
        val parsedEnv = try {
            json.decodeFromDynamic<Env>(env)
        } catch (cause: SerializationException) {
            return promise { serverError(json = json).toJsResponse() }
        }

        tokenProvider.apiToken = parsedEnv.forecastApiKey
        attestationConfig.googleServiceAccountJson = parsedEnv.googleServiceAccountJson
        attestationConfig.appleAppId = parsedEnv.appleAppId

        if (cacheProvider.cache == null) {
            val kvNamespace: dynamic = env.FORECAST_CACHE
            if (kvNamespace != null) {
                cacheProvider.cache = KvForecastCache(kvNamespace)
            }
        }

        return promise { router.handle(request.toServerRequest()).toJsResponse() }
    }
}
