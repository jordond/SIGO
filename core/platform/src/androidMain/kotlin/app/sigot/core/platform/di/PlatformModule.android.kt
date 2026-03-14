package app.sigot.core.platform.di

import app.sigot.core.platform.AttestationTokenProvider
import app.sigot.core.platform.attestation.PlayIntegrityTokenProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

internal actual fun Module.platformConfig() {
    single<AttestationTokenProvider> { PlayIntegrityTokenProvider(androidContext()) }
}
