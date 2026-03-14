package app.sigot.core.platform.attestation

import app.sigot.core.platform.AttestationTokenProvider

internal class NoopAttestationTokenProvider : AttestationTokenProvider {
    override val platform: String? = null

    override suspend fun getToken(requestHash: String): String? = null

    override fun resetAttestation() = Unit
}
