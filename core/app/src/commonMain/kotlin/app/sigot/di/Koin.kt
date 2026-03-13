package app.sigot.di

import app.sigot.core.api.client.apiClientModule
import app.sigot.core.config.configModule
import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.di.platformModule
import app.sigot.forecast.forecastAppModule
import app.sigot.forecast.ui.forecastUiModule
import app.sigot.location.locationModule
import app.sigot.onboarding.onboardingModule
import app.sigot.settings.settingsModule
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

@OptIn(ExperimentalKermitApi::class)
public fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        appDeclaration()

        logger(KermitKoinLogger(Logger.withTag(tag = "Koin")))

        Logger.addLogWriter(CrashlyticsLogWriter())

        modules(
            appModule(),
            // Core
            apiClientModule(),
            configModule(),
            foundationModule(),
            platformModule(),
            // Feature
            forecastAppModule(),
            forecastUiModule(),
            locationModule(),
            onboardingModule(),
            settingsModule(),
        )
    }
