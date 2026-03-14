package app.sigot.core.api.server.attestation

public interface AttestationVerifier {
    public val platform: AttestationPlatform

    public suspend fun verify(
        token: String,
        clientId: String,
    ): AttestationResult
}
