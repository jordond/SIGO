package now.shouldigooutside.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.header
import now.shouldigooutside.core.api.client.apiClientModule
import now.shouldigooutside.core.api.model.http.ApiHeaders
import now.shouldigooutside.core.config.configModule
import now.shouldigooutside.core.domain.domainModule
import now.shouldigooutside.core.foundation.di.foundationModule
import now.shouldigooutside.core.platform.ClientIdProvider
import now.shouldigooutside.core.platform.di.getKoinInstance
import now.shouldigooutside.core.platform.di.networkModule
import now.shouldigooutside.core.platform.di.platformModule
import now.shouldigooutside.core.widget.widgetModule
import now.shouldigooutside.forecast.forecastAppModule
import now.shouldigooutside.forecast.ui.forecastUiModule
import now.shouldigooutside.location.locationModule
import now.shouldigooutside.onboarding.onboardingModule
import now.shouldigooutside.settings.settingsModule
import now.shouldigooutside.whatsnew.whatsNewModule
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
            domainModule(),
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
            widgetModule(),
            // Feature
            forecastAppModule(),
            forecastUiModule(),
            locationModule(),
            onboardingModule(),
            settingsModule(),
            whatsNewModule(),
        )
    }

internal expect fun configureCrashlytics()
