package app.sigot.core.api.server

import app.sigot.core.api.server.attestation.AppAttestVerifier
import app.sigot.core.api.server.attestation.AttestationConfig
import app.sigot.core.api.server.attestation.AttestationRegistrar
import app.sigot.core.api.server.attestation.AttestationService
import app.sigot.core.api.server.attestation.AttestationVerifier
import app.sigot.core.api.server.attestation.GoogleAuthProvider
import app.sigot.core.api.server.attestation.KvAttestationRegistrar
import app.sigot.core.api.server.attestation.PlayIntegrityVerifier
import app.sigot.core.api.server.cache.CacheProvider
import app.sigot.core.api.server.cache.ForecastCacheProvider
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

public fun jsApiServerModule(): Module =
    module {
        includes(commonApiServerModule())
        single { ForecastCacheProvider() } bind CacheProvider::class

        // Attestation config (mutable, set from Worker env)
        single { AttestationConfig() }

        // Attestation verifiers (multi-bind via `bind`)
        single {
            PlayIntegrityVerifier(
                httpClient = get(),
                packageName = "now.shouldigooutside",
                googleAuthProvider = get(),
                json = get(),
            )
        } bind AttestationVerifier::class

        single {
            AppAttestVerifier(
                cacheProvider = get(),
                config = get(),
                json = get(),
            )
        } bind AttestationVerifier::class

        single {
            GoogleAuthProvider(
                config = get(),
                httpClient = get(),
                json = get(),
            )
        }

        single {
            AttestationService(verifiers = getAll<AttestationVerifier>())
        }

        single<AttestationRegistrar> {
            KvAttestationRegistrar(
                cacheProvider = get(),
                config = get(),
                json = get(),
            )
        }
    }
