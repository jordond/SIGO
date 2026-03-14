package app.sigot.core.platform.attestation

import app.sigot.core.platform.AttestationTokenProvider
import app.sigot.core.platform.ClientIdProvider
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import platform.DeviceCheck.DCAppAttestService
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class AppAttestTokenProvider(
    private val apiClient: HttpClient,
    private val clientIdProvider: ClientIdProvider,
    private val json: Json,
    private val backendUrl: String,
) : AttestationTokenProvider {
    private val logger = Logger.withTag("AppAttestTokenProvider")
    private val attestService = DCAppAttestService.sharedService

    override val platform: String = "ios"

    private var keyId: String? = null
    private var isAttested: Boolean = false

    override suspend fun getToken(requestHash: String): String? {
        if (!attestService.isSupported()) {
            logger.d { "App Attest not supported on this device" }
            return null
        }

        return try {
            val currentKeyId = getOrCreateKeyId()
            if (!isAttested) {
                performAttestation(currentKeyId)
            }
            generateAssertion(currentKeyId, requestHash)
        } catch (e: Exception) {
            logger.w(e) { "App Attest token generation failed" }
            null
        }
    }

    private suspend fun getOrCreateKeyId(): String {
        keyId?.let { return it }

        // Generate a new key
        val newKeyId = suspendCancellableCoroutine<String> { continuation ->
            attestService.generateKeyWithCompletionHandler { generatedKeyId, error ->
                if (error != null || generatedKeyId == null) {
                    continuation.resumeWithException(
                        Exception("Failed to generate App Attest key: ${error?.localizedDescription}"),
                    )
                } else {
                    continuation.resume(generatedKeyId)
                }
            }
        }

        keyId = newKeyId
        return newKeyId
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun performAttestation(keyId: String) {
        val clientId = clientIdProvider.clientId()

        // 1. Request nonce from server
        val nonceResponse = apiClient.get("$backendUrl/v1/attest") {
            header("X-Client-ID", clientId)
        }
        if (!nonceResponse.status.isSuccess()) {
            throw Exception("Failed to get attestation nonce: ${nonceResponse.status}")
        }

        val nonceBody = json.decodeFromString<NonceResponse>(nonceResponse.bodyAsText())
        val nonce = nonceBody.nonce

        // 2. Hash the nonce + clientId for the client data
        val clientData = "$nonce$clientId"
        val clientDataHash = sha256(clientData.encodeToByteArray())

        // 3. Attest the key with Apple
        val nsDataHash = clientDataHash.toNSData()
        val attestationObject = suspendCancellableCoroutine<NSData> { continuation ->
            attestService.attestKey(keyId, nsDataHash) { attestation, error ->
                if (error != null || attestation == null) {
                    continuation.resumeWithException(
                        Exception("App Attest attestation failed: ${error?.localizedDescription}"),
                    )
                } else {
                    continuation.resume(attestation)
                }
            }
        }

        // 4. Send attestation to server
        val attestationBase64 = attestationObject.base64EncodedStringWithOptions(0u)
        val response = apiClient.post("$backendUrl/v1/attest") {
            header("X-Client-ID", clientId)
            contentType(ContentType.Application.Json)
            setBody(
                json.encodeToString(
                    AttestRequest(
                        attestation = attestationBase64,
                        keyId = keyId,
                        challenge = nonce,
                    ),
                ),
            )
        }

        if (!response.status.isSuccess()) {
            throw Exception("Server attestation registration failed: ${response.status}")
        }

        isAttested = true
        logger.i { "App Attest registration successful" }
    }

    @OptIn(ExperimentalForeignApi::class)
    private suspend fun generateAssertion(
        keyId: String,
        requestHash: String,
    ): String? {
        val clientDataHash = sha256(requestHash.encodeToByteArray())
        val nsDataHash = clientDataHash.toNSData()

        val assertionData = suspendCancellableCoroutine<NSData> { continuation ->
            attestService.generateAssertion(keyId, nsDataHash) { assertion, error ->
                if (error != null || assertion == null) {
                    continuation.resumeWithException(
                        Exception("App Attest assertion failed: ${error?.localizedDescription}"),
                    )
                } else {
                    continuation.resume(assertion)
                }
            }
        }

        return assertionData.base64EncodedStringWithOptions(0u)
    }

    @Serializable
    private data class NonceResponse(
        val nonce: String,
    )

    @Serializable
    private data class AttestRequest(
        val attestation: String,
        val keyId: String,
        val challenge: String,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun sha256(input: ByteArray): ByteArray {
    val digest = ByteArray(32) // CC_SHA256_DIGEST_LENGTH
    input.usePinned { pinnedInput ->
        digest.usePinned { pinnedDigest ->
            // Use CommonCrypto via cinterop
            platform.CommonCrypto.CC_SHA256(
                pinnedInput.addressOf(0),
                input.size.toUInt(),
                pinnedDigest.addressOf(0),
            )
        }
    }
    return digest
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),
            length = size.toULong(),
        )
    }
}
