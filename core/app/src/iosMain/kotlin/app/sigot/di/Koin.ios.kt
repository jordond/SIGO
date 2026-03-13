package app.sigot.di

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook

internal actual fun configureCrashlytics() {
    enableCrashlytics()
    setCrashlyticsUnhandledExceptionHook()
}
