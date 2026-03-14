package app.sigot.core.api.server.attestation

internal class FakeAttestationVerifier(
    override val platform: AttestationPlatform,
    private val result: AttestationResult = AttestationResult.Attested(platform),
) : AttestationVerifier {
    var lastToken: String? = null
        private set
    var lastClientId: String? = null
        private set
    var lastRequestHash: String? = null
        private set

    override suspend fun verify(
        token: String,
        clientId: String,
        requestHash: String,
    ): AttestationResult {
        lastToken = token
        lastClientId = clientId
        lastRequestHash = requestHash
        return result
    }
}
