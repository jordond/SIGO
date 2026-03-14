package app.sigot.core.api.server.attestation

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

internal class PlayIntegrityVerifier(
    private val httpClient: HttpClient,
    private val packageName: String,
    private val googleAuthProvider: GoogleAuthProvider,
    private val json: Json,
) : AttestationVerifier {
    private val logger = Logger.withTag("PlayIntegrityVerifier")

    override val platform: AttestationPlatform = AttestationPlatform.PLAY_INTEGRITY

    override suspend fun verify(
        token: String,
        clientId: String,
    ): AttestationResult {
        val accessToken = googleAuthProvider.getAccessToken()

        val url = "https://playintegrity.googleapis.com/v1/$packageName:decodeIntegrityToken"

        val response = httpClient.post(url) {
            bearerAuth(accessToken)
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(DecodeIntegrityTokenRequest(integrityToken = token)))
        }

        if (!response.status.isSuccess()) {
            val body = response.bodyAsText()
            logger.w { "Play Integrity API returned ${response.status}: $body" }
            return AttestationResult.Failed("Google API error: ${response.status}")
        }

        val payload = try {
            json.decodeFromString<DecodeIntegrityTokenResponse>(response.bodyAsText()).tokenPayloadExternal
        } catch (e: Exception) {
            logger.w(e) { "Failed to parse Play Integrity response" }
            return AttestationResult.Failed("Failed to parse response: ${e.message}")
        }

        // Verify package name
        if (payload.requestDetails?.requestPackageName != packageName) {
            logger.w {
                "Package name mismatch: expected=$packageName got=${payload.requestDetails?.requestPackageName}"
            }
            return AttestationResult.Failed("Package name mismatch")
        }

        // Verify app recognition
        val appVerdict = payload.appIntegrity?.appRecognitionVerdict
        if (appVerdict != "PLAY_RECOGNIZED") {
            logger.w { "App not recognized: $appVerdict" }
            return AttestationResult.Failed("App not recognized: $appVerdict")
        }

        // Verify device integrity
        val deviceVerdicts = payload.deviceIntegrity?.deviceRecognitionVerdict ?: emptyList()
        if ("MEETS_DEVICE_INTEGRITY" !in deviceVerdicts) {
            logger.w { "Device integrity check failed: $deviceVerdicts" }
            return AttestationResult.Failed("Device integrity check failed")
        }

        return AttestationResult.Attested(AttestationPlatform.PLAY_INTEGRITY)
    }
}
