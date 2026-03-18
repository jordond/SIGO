package now.shouldigooutside.core.api.client

import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestPipeline
import now.shouldigooutside.core.api.client.internal.DefaultApiClient
import now.shouldigooutside.core.api.model.http.ApiHeaders
import now.shouldigooutside.core.platform.ClientIdProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

public fun apiClientModule(): Module =
    module {
        factoryOf(::DefaultApiClient) bind ApiClient::class

        single<HttpClient> {
            val existing = get<HttpClient>()
            val clientIdProvider = get<ClientIdProvider>()
            existing.also { client ->
                client.requestPipeline.intercept(HttpRequestPipeline.State) {
                    context.headers.append(ApiHeaders.CLIENT_ID, clientIdProvider.clientId())
                    proceed()
                }
            }
        }
    }
