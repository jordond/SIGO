package app.sigot.core.platform.di

import app.sigot.core.platform.AttestationTokenProvider
import app.sigot.core.platform.attestation.NoopAttestationTokenProvider
import org.koin.core.module.Module

internal actual fun Module.platformConfig() {
    single<AttestationTokenProvider> { NoopAttestationTokenProvider() }
}
