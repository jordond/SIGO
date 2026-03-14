package app.sigot.core.api.server.attestation

import co.touchlab.kermit.Logger

public class AttestationService(
    private val verifiers: List<AttestationVerifier>,
) {
    private val logger = Logger.withTag("AttestationService")

    public suspend fun verify(
        token: String?,
        platform: String?,
        clientId: String,
        requestHash: String,
    ): AttestationResult {
        if (token == null || platform == null) return AttestationResult.Unattested
        val verifier = verifiers.find { it.platform.headerValue == platform }
            ?: return AttestationResult.Unattested
        return try {
            verifier.verify(token, clientId, requestHash)
        } catch (e: Exception) {
            logger.w(e) { "Attestation verification failed for platform=$platform clientId=$clientId" }
            AttestationResult.Failed(e.message ?: "Verification error")
        }
    }
}
