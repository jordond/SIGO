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
import now.shouldigooutside.core.api.server.ApiRoute
import now.shouldigooutside.core.api.server.ApiRouter
import now.shouldigooutside.core.api.server.DefaultApiRouter
import now.shouldigooutside.core.api.server.cache.CacheProvider
import now.shouldigooutside.core.api.server.routes.VersionRoute
import now.shouldigooutside.core.api.server.routes.forecast.ForecastRoute
import now.shouldigooutside.core.api.server.routes.forecast.score.ForecastScoreRoute
import now.shouldigooutside.core.domain.forecast.DefaultScoreCalculator
import now.shouldigooutside.core.domain.forecast.ForecastRepo
import now.shouldigooutside.core.domain.forecast.GetForecastUseCase
import now.shouldigooutside.core.domain.forecast.ScoreCalculator
import now.shouldigooutside.core.foundation.analytics.AnalyticsLogger
import now.shouldigooutside.core.platform.isDebug
import now.shouldigooutside.forecast.data.DefaultForecastRepo
import now.shouldigooutside.forecast.data.source.ForecastSource
import now.shouldigooutside.forecast.data.source.QueryCostLogger
import now.shouldigooutside.forecast.data.source.visualcrossing.DefaultVisualCrossingApi
import now.shouldigooutside.forecast.data.source.visualcrossing.VisualCrossingApi
import now.shouldigooutside.forecast.data.source.visualcrossing.VisualCrossingForecastSource
import now.shouldigooutside.forecast.domain.DefaultGetForecastUseCase
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
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

            scoped<QueryCostLogger> {
                val analytics = get<AnalyticsLogger>()
                QueryCostLogger { cost ->
                    analytics.log("Query cost", mapOf("cost" to cost.toString()))
                }
            }

            scoped<ScoreCalculator> { DefaultScoreCalculator() }
            scoped<VisualCrossingApi> { DefaultVisualCrossingApi(get(), get()) }
            scoped<ForecastSource> { VisualCrossingForecastSource(get(), get(), get()) }
            scoped<ForecastRepo> { DefaultForecastRepo(get(), null) }
            scoped<GetForecastUseCase> { DefaultGetForecastUseCase(get()) }

            scoped { VersionRoute(get()) } bind ApiRoute::class
            scoped { ForecastRoute(get(), get(), get()) } bind ApiRoute::class
            scoped { ForecastScoreRoute(get(), get(), get(), get()) } bind ApiRoute::class

            scoped<ApiRouter> {
                DefaultApiRouter(
                    routes = getAll(),
                    json = get(),
                    cacheProvider = get(),
                    rateLimiter = get(),
                    corsHandler = get(),
                )
            }
        }
    }
