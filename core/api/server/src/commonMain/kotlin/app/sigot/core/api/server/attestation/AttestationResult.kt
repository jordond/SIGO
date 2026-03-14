package app.sigot.core.api.server.attestation

public sealed interface AttestationResult {
    public data object Unattested : AttestationResult

    public data class Attested(
        val platform: AttestationPlatform,
    ) : AttestationResult

    public data class Failed(
        val reason: String,
    ) : AttestationResult
}
