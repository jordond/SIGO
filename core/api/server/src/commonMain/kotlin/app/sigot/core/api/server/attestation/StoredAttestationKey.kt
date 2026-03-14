package app.sigot.core.api.server.attestation

import kotlinx.serialization.Serializable

@Serializable
internal data class StoredAttestationKey(
    val publicKeyBase64: String,
    val counter: Long = 0,
)
