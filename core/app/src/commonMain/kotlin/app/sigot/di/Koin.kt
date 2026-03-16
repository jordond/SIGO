package app.sigot.di

import app.sigot.core.api.client.apiClientModule
import app.sigot.core.api.model.http.ApiHeaders
import app.sigot.core.config.configModule
import app.sigot.core.foundation.di.foundationModule
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

internal expect fun configureCrashlytics()
