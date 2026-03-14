package app.sigot.core.api.server.attestation

/**
 * Mutable holder for attestation configuration, set from Worker env on first request.
 * Follows the same pattern as [app.sigot.core.api.server.cache.ForecastCacheProvider].
 */
internal class AttestationConfig {
    var googleServiceAccountJson: String? = null
    var appleAppId: String? = null
}
