package now.shouldigooutside.core.foundation.di

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import now.shouldigooutside.core.foundation.DefaultNowProvider
import now.shouldigooutside.core.foundation.DefaultTimezoneProvider
import now.shouldigooutside.core.foundation.NowProvider
import now.shouldigooutside.core.foundation.TimezoneProvider
import now.shouldigooutside.core.foundation.analytics.AnalyticsLogger
import now.shouldigooutside.core.foundation.analytics.KermitAnalyticsLogger
import now.shouldigooutside.core.foundation.initalize.DefaultInitializer
import now.shouldigooutside.core.foundation.initalize.Initializer
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.random.Random

public fun foundationModule(): Module =
    module {
        factory { CoroutineScope(Dispatchers.Default + SupervisorJob()) }

        single {
            val logger = KermitAnalyticsLogger()

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
