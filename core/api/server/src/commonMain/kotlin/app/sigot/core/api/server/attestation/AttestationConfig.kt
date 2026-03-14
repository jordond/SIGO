package app.sigot.core.api.server.attestation

/**
 * Mutable holder for attestation configuration, set from the environment on first request.
 * Follows the initialization pattern where the config object is created at DI time and
 * populated lazily from environment variables before first use.
 */
internal class AttestationConfig {
    var googleServiceAccountJson: String? = null
    var appleAppId: String? = null
}
