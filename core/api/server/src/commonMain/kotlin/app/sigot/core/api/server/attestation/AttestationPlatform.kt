package app.sigot.core.api.server.attestation

public enum class AttestationPlatform(
    public val headerValue: String,
) {
    PLAY_INTEGRITY("android"),
    APP_ATTEST("ios"),
}
