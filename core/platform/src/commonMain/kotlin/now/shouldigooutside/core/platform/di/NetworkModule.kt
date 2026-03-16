package now.shouldigooutside.core.platform.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import now.shouldigooutside.core.platform.isDebug
import org.koin.core.module.Module
import org.koin.dsl.module

private val taggedLogger = co.touchlab.kermit.Logger
    .withTag("Ktor")

public val defaultJson: Json = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
}

public fun networkModule(httpClientConfig: HttpClientConfig<*>.() -> Unit = {}): Module =
    module {
        single<Json> { defaultJson }

        single {
            val json = get<Json>()
            HttpClient {
                expectSuccess = true

                install(ContentNegotiation) {
                    json(json)
                }

                install(Logging) {
                    level = if (isDebug) LogLevel.INFO else LogLevel.NONE
                    logger = object : Logger {
                        override fun log(message: String) {
                            taggedLogger.d { message }
                        }
                    }
                }

                httpClientConfig()
            }
        }
    }
