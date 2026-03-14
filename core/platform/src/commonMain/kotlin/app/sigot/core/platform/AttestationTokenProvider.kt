package app.sigot.core.platform

public interface AttestationTokenProvider {
    /** The platform identifier sent in X-Attestation-Platform header, or null if attestation not supported. */
    public val platform: String?

    /** Generate an attestation token for the given request hash, or null if unavailable. */
    public suspend fun getToken(requestHash: String): String?
}
