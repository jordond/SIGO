package now.shouldigooutside.api.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import now.shouldigooutside.core.api.server.scopedApiRouterDefinitions
import now.shouldigooutside.forecast.scopedForecastDefinitions
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.onClose

internal fun requestModule(): Module =
    module {
        scope<RequestScope> {
            // Dispatchers.Unconfined keeps continuations on whatever microtask
            // resolved the previous suspension. On Cloudflare Workers that is
            // always the request's I/O context, which avoids the cross-request
            // promise resolution traps that Dispatchers.Default's setTimeout-
            // backed scheduling can introduce.
            scoped<CoroutineScope> {
                CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
            } onClose { it?.cancel() }

            scoped<HttpClient> {
                HttpClient {
                    expectSuccess = true
                    install(ContentNegotiation) { json(get()) }
                }
            } onClose { it?.close() }

            scopedForecastDefinitions()
            scopedApiRouterDefinitions()
        }
    }
