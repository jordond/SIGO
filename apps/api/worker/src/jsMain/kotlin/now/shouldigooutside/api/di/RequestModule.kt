package now.shouldigooutside.api.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import now.shouldigooutside.core.api.server.scopedApiRouterDefinitions
import now.shouldigooutside.core.platform.isDebug
import now.shouldigooutside.forecast.scopedForecastDefinitions
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.onClose
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.plugins.logging.Logger as KtorLogger

private val ktorLogTag = KermitLogger.withTag("Ktor")

internal fun requestModule(): Module =
    module {
        scope<RequestScope> {
            scoped<CoroutineScope> {
                CoroutineScope(SupervisorJob() + Dispatchers.Default)
            } onClose { it?.cancel() }

            scoped<HttpClient> {
                HttpClient {
                    expectSuccess = true
                    install(ContentNegotiation) { json(get()) }
                    install(Logging) {
                        level = if (isDebug) LogLevel.INFO else LogLevel.NONE
                        logger = object : KtorLogger {
                            override fun log(message: String) {
                                ktorLogTag.d { message }
                            }
                        }
                    }
                }
            } onClose { it?.close() }

            scopedForecastDefinitions()
            scopedApiRouterDefinitions()
        }
    }
