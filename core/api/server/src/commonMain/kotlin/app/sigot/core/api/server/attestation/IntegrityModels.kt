package app.sigot.core.api.server.attestation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DecodeIntegrityTokenRequest(
    @SerialName("integrity_token")
    val integrityToken: String,
)

@Serializable
internal data class DecodeIntegrityTokenResponse(
    val tokenPayloadExternal: IntegrityTokenPayload,
)

@Serializable
internal data class IntegrityTokenPayload(
    val requestDetails: RequestDetails? = null,
    val appIntegrity: AppIntegrity? = null,
    val deviceIntegrity: DeviceIntegrity? = null,
    val accountDetails: AccountDetails? = null,
)

@Serializable
internal data class RequestDetails(
    val requestPackageName: String? = null,
    val nonce: String? = null,
)

@Serializable
internal data class AppIntegrity(
    val appRecognitionVerdict: String? = null,
)

@Serializable
internal data class DeviceIntegrity(
    val deviceRecognitionVerdict: List<String> = emptyList(),
)

@Serializable
internal data class AccountDetails(
    val appLicensingVerdict: String? = null,
)
