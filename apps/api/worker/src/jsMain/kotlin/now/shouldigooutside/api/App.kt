package now.shouldigooutside.api

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import now.shouldigooutside.api.di.RequestScope
import now.shouldigooutside.api.provider.EnvKvCache
import now.shouldigooutside.api.provider.WorkerCacheProvider
import now.shouldigooutside.api.provider.WorkerTokenProvider
import now.shouldigooutside.core.api.server.ApiRouter
import now.shouldigooutside.core.api.server.cache.CacheProvider
import now.shouldigooutside.core.api.server.http.toJsResponse
import now.shouldigooutside.core.api.server.http.toServerRequest
import now.shouldigooutside.core.api.server.util.serverError
import now.shouldigooutside.core.domain.forecast.ApiTokenProvider
import org.koin.core.Koin
import org.w3c.fetch.Request
import org.w3c.fetch.Response
import kotlin.js.Promise
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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

@OptIn(ExperimentalSerializationApi::class, ExperimentalUuidApi::class, DelicateCoroutinesApi::class)
class DefaultApp(
    private val koin: Koin,
) : App {
    private val json: Json = koin.get()

    override fun handle(
        request: Request,
        env: dynamic,
    ): Promise<Response> {
        val parsedEnv = try {
            json.decodeFromDynamic<Env>(env)
        } catch (cause: SerializationException) {
            Logger.e(cause) { "Failed to deserialize the env: $env" }
            return GlobalScope.promise { serverError(json = json).toJsResponse() }
        }

        val scopeId = Uuid.random().toString()
        val koinScope = koin.createScope<RequestScope>(scopeId)

        val tokenProvider: ApiTokenProvider = WorkerTokenProvider(parsedEnv.forecastApiKey)
        koinScope.declare(tokenProvider)

        val kvBinding: dynamic = env.FORECAST_CACHE
        val cacheProvider: CacheProvider = WorkerCacheProvider(
            cache = if (kvBinding != null) EnvKvCache(kvBinding) else null,
        )
        koinScope.declare(cacheProvider)

        val requestScope = koinScope.get<CoroutineScope>()
        val router = koinScope.get<ApiRouter>()

        return requestScope.promise {
            try {
                router.handle(request.toServerRequest()).toJsResponse()
            } finally {
                koinScope.close()
            }
        }
    }
}
