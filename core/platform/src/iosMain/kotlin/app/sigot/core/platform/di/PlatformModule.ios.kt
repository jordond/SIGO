package app.sigot.core.platform.di

import app.sigot.core.model.settings.InternalSettings
import app.sigot.core.platform.AttestationTokenProvider
import app.sigot.core.platform.attestation.AppAttestTokenProvider
import app.sigot.core.platform.store.Store
import org.koin.core.module.Module

internal actual fun Module.platformConfig() {
    single<AttestationTokenProvider> {
        AppAttestTokenProvider(
            apiClient = get(),
            clientIdProvider = get(),
            json = get(),
            backendUrl = InternalSettings.DefaultBackendApiUrl,
            attestStateStore = Store.storeOf(
                filename = "app-attest-state.json",
                type = Store.Type.Persistent,
            ),
        )
    }
}
