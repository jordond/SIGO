package app.sigot.api.di

import app.sigot.core.foundation.di.foundationModule
import app.sigot.forecast.forecastBackendModule
import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

internal fun initKoin() =
    startKoin {
        logger(KermitKoinLogger(Logger.withTag("Koin")))

        modules(
            workerModule(),
            foundationModule(),
            forecastBackendModule(),
        )
    }

private fun workerModule() =
    module {
        single { "Foo Bar Biz Baz" }
    }
