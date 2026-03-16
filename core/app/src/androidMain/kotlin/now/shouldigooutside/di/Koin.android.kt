package now.shouldigooutside.di

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter

@OptIn(ExperimentalKermitApi::class)
internal actual fun configureCrashlytics() {
    enableCrashlytics()
    Logger.addLogWriter(CrashlyticsLogWriter())
}
