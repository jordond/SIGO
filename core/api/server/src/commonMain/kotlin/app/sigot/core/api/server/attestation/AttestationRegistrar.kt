package app.sigot.core.api.server.attestation

/**
 * Handles one-time device attestation registration (App Attest).
 * Interface in commonMain, implementation in jsMain.
 */
public interface AttestationRegistrar {
    /** Generate a nonce for attestation challenge. */
    public suspend fun generateNonce(clientId: String): String?

    /** Register an attested device key. */
    public suspend fun registerAttestation(
        clientId: String,
        attestationBase64: String,
        keyId: String,
        challenge: String,
    ): Boolean
}
