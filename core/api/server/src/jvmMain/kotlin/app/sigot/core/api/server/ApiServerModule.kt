@file:JvmName("JvmApiServerModule")

package app.sigot.core.api.server

import app.sigot.core.api.server.attestation.AttestationConfig
import app.sigot.core.api.server.attestation.AttestationRegistrar
import app.sigot.core.api.server.attestation.AttestationVerifier
import app.sigot.core.api.server.attestation.GoogleAuthProvider
import app.sigot.core.api.server.attestation.JvmAppAttestVerifier
import app.sigot.core.api.server.attestation.JvmAttestationRegistrar
import app.sigot.core.api.server.attestation.JvmGoogleAuthProvider
import app.sigot.core.api.server.attestation.PlayIntegrityVerifier
import app.sigot.core.api.server.cache.CacheProvider
import app.sigot.core.api.server.cache.InMemoryApiCache
import app.sigot.core.api.server.cache.JvmCacheProvider
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public fun jvmApiServerModule(): Module =
    module {
        includes(commonApiServerModule())
        single<CacheProvider> { JvmCacheProvider(cache = InMemoryApiCache(scope = get())) }

        single { AttestationConfig() }

        single<GoogleAuthProvider> {
            JvmGoogleAuthProvider(config = get(), httpClient = get(), json = get())
        }

        single {
            PlayIntegrityVerifier(
                httpClient = get(),
                packageName = "now.shouldigooutside",
                googleAuthProvider = get(),
                json = get(),
            )
        } bind AttestationVerifier::class

        single {
            JvmAppAttestVerifier(
                cacheProvider = get(),
                config = get(),
                json = get(),
            )
        } bind AttestationVerifier::class

        single<AttestationRegistrar> {
            JvmAttestationRegistrar(cacheProvider = get(), json = get())
        }
    }
