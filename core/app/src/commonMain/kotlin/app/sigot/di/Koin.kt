package app.sigot.di

import app.sigot.core.api.client.apiClientModule
import app.sigot.core.api.server.http.ApiHeaders
import app.sigot.core.config.configModule
import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.AttestationTokenProvider
import app.sigot.core.platform.ClientIdProvider
import app.sigot.core.platform.di.getKoinInstance
import app.sigot.core.platform.di.networkModule
import app.sigot.core.platform.di.platformModule
import app.sigot.forecast.forecastAppModule
import app.sigot.forecast.ui.forecastUiModule
import app.sigot.location.locationModule
import app.sigot.onboarding.onboardingModule
import app.sigot.settings.settingsModule
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

public fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        appDeclaration()

        logger(KermitKoinLogger(Logger.withTag(tag = "Koin")))

        configureCrashlytics()

        modules(
            appModule(),
            // Core
            apiClientModule(),
            configModule(),
            foundationModule(),
            networkModule {
                install(
                    createClientPlugin("ClientIdPlugin") {
                        onRequest { request, _ ->
                            val clientId = getKoinInstance<ClientIdProvider>().clientId()
                            request.header(ApiHeaders.CLIENT_ID, clientId)
                        }
                    },
                )
                install(
                    createClientPlugin("AttestationPlugin") {
                        onRequest { request, _ ->
                            val provider = getKoinInstance<AttestationTokenProvider>()
                            val platform = provider.platform ?: return@onRequest
                            val requestHash = computeRequestHash(request)
                            val token = provider.getToken(requestHash) ?: return@onRequest
                            request.header(ApiHeaders.ATTESTATION_TOKEN, token)
                            request.header(ApiHeaders.ATTESTATION_PLATFORM, platform)
                        }
                    },
                )
            },
            platformModule(),
            // Feature
            forecastAppModule(),
            forecastUiModule(),
            locationModule(),
            onboardingModule(),
            settingsModule(),
        )
    }

private fun computeRequestHash(request: io.ktor.client.request.HttpRequestBuilder): String {
    val method = request.method.value
    val url = request.url.buildString()
    val input = "$method$url"
    // Simple hash using Kotlin's built-in hashCode as a fallback
    // A proper SHA-256 implementation can be added later
    return input
        .hashCode()
        .toUInt()
        .toString(16)
        .padStart(8, '0')
}

internal expect fun configureCrashlytics()
