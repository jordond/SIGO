package app.sigot.core.foundation.di

import app.sigot.core.foundation.DefaultNowProvider
import app.sigot.core.foundation.DefaultTimezoneProvider
import app.sigot.core.foundation.NowProvider
import app.sigot.core.foundation.TimezoneProvider
import app.sigot.core.foundation.analytics.AnalyticsLogger
import app.sigot.core.foundation.analytics.KermitAnalyticsLogger
import app.sigot.core.foundation.analytics.NoopAnalyticsLogger
import app.sigot.core.foundation.initalize.DefaultInitializer
import app.sigot.core.foundation.initalize.Initializer
import app.sigot.core.platform.isDebug
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.random.Random

public fun foundationModule(): Module =
    module {
        factory { CoroutineScope(Dispatchers.Default + SupervisorJob()) }

        single {
            val logger = if (isDebug) {
                KermitAnalyticsLogger()
            } else {
                // TODO: Replace with real analytics
                NoopAnalyticsLogger()
            }

            Logger.addLogWriter(
                object : LogWriter() {
                    override fun log(
                        severity: Severity,
                        message: String,
                        tag: String,
                        throwable: Throwable?,
                    ) {
                        if (severity >= Severity.Error) {
                            logger.log(message, "tag" to tag, "error" to (throwable?.message ?: "n/a"))
                        }
                    }
                },
            )

            logger
        } bind AnalyticsLogger::class

        factory<Random> { Random.Default }

        single { DefaultInitializer(getAll()) } bind Initializer::class

        singleOf(::DefaultTimezoneProvider) bind TimezoneProvider::class
        singleOf(::DefaultNowProvider) bind NowProvider::class
    }
