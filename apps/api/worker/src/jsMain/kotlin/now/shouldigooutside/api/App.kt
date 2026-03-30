package now.shouldigooutside.api

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import now.shouldigooutside.api.provider.KvCache
import now.shouldigooutside.api.provider.KvCacheProvider
import now.shouldigooutside.api.provider.WorkerTokenProvider
import now.shouldigooutside.core.api.server.ApiRouter
import now.shouldigooutside.core.api.server.http.toJsResponse
import now.shouldigooutside.core.api.server.http.toServerRequest
import now.shouldigooutside.core.api.server.util.serverError
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
)

@OptIn(ExperimentalSerializationApi::class)
class DefaultApp(
    scope: CoroutineScope,
    private val router: ApiRouter,
    private val tokenProvider: WorkerTokenProvider,
    private val cacheProvider: KvCacheProvider,
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
            Logger.e(cause) { "Failed to deserialize the env: $env" }
            return promise { serverError(json = json).toJsResponse() }
        }

        tokenProvider.apiToken = parsedEnv.forecastApiKey

        if (cacheProvider.cache == null) {
            val kvNamespace: dynamic = env.FORECAST_CACHE
            if (kvNamespace != null) {
                cacheProvider.cache = KvCache(kvNamespace)
            }
        }

        return promise { router.handle(request.toServerRequest()).toJsResponse() }
    }
}
