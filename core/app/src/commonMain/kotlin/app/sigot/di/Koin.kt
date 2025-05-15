package app.sigot.di

import app.sigot.core.foundation.di.foundationModule
import app.sigot.core.platform.di.platformModule
import app.sigot.forecast.data.forecastDataModule
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()

        logger(KermitKoinLogger(Logger.withTag(tag = "Koin")))

        modules(
            // Core
            foundationModule(),
            platformModule(),
            // Feature
            forecastDataModule(),
        )
    }
}
