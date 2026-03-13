package app.sigot.di

import co.touchlab.crashkios.crashlytics.enableCrashlytics

internal actual fun configureCrashlytics() {
    enableCrashlytics()
}
