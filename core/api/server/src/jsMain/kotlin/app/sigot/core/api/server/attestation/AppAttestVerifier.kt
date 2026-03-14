package app.sigot.core.api.server.attestation

import app.sigot.core.api.server.cache.CacheProvider
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise
import kotlin.time.Duration.Companion.days

internal class AppAttestVerifier(
    private val cacheProvider: CacheProvider,
    private val config: AttestationConfig,
    private val json: Json,
) : AttestationVerifier {
    private val logger = Logger.withTag("AppAttestVerifier")

    override val platform: AttestationPlatform = AttestationPlatform.APP_ATTEST

    override suspend fun verify(
        token: String,
        clientId: String,
        requestHash: String,
    ): AttestationResult {
        val cache = cacheProvider.cache
            ?: return AttestationResult.Failed("Cache unavailable")

        // Look up stored public key for this client
        val storedKeyJson = cache.get("attest:pk:$clientId")
            ?: return AttestationResult.Unattested // Not registered yet

        val storedKey = try {
            json.decodeFromString<StoredAttestationKey>(storedKeyJson)
        } catch (e: Exception) {
            logger.w(e) { "Failed to parse stored attestation key for $clientId" }
            return AttestationResult.Failed("Invalid stored key")
        }

        // Base64-decode the assertion token
        val assertionBytes = try {
            base64DecodeToBytes(token)
        } catch (e: Exception) {
            return AttestationResult.Failed("Invalid assertion encoding")
        }

        // CBOR-decode the assertion
        val assertion = try {
            cborDecode(assertionBytes)
        } catch (e: Exception) {
            logger.w(e) { "CBOR decode failed for assertion" }
            return AttestationResult.Failed("Invalid CBOR assertion")
        }

        // Extract signature and authenticatorData from the assertion
        val signature = assertion.signature as? Uint8Array
            ?: return AttestationResult.Failed("Missing signature in assertion")
        val authenticatorData = assertion.authenticatorData as? Uint8Array
            ?: return AttestationResult.Failed("Missing authenticatorData in assertion")

        // Verify the counter (anti-replay)
        // authenticatorData bytes 33-36 contain the counter (big-endian uint32)
        val counterValue = if (authenticatorData.length >= 37) {
            (authenticatorData[33].toLong() and 0xFF shl 24) or
                (authenticatorData[34].toLong() and 0xFF shl 16) or
                (authenticatorData[35].toLong() and 0xFF shl 8) or
                (authenticatorData[36].toLong() and 0xFF)
        } else {
            0L
        }

        if (counterValue <= storedKey.counter) {
            logger.w {
                "Counter replay detected for $clientId: received=$counterValue stored=${storedKey.counter}"
            }
            return AttestationResult.Failed("Counter replay detected")
        }

        // Compute SHA-256(clientData) where clientData = requestHash
        // Per Apple spec, signature covers authenticatorData || SHA-256(clientData)
        val clientDataHash = sha256(requestHash.encodeToByteArray())

        // Import the stored public key for ES256 verification
        val publicKeyBytes = base64DecodeToArrayBuffer(storedKey.publicKeyBase64)
        val verified = try {
            verifyES256Signature(publicKeyBytes, authenticatorData, clientDataHash, signature)
        } catch (e: Exception) {
            logger.w(e) { "Signature verification failed for $clientId" }
            return AttestationResult.Failed("Signature verification failed: ${e.message}")
        }

        if (!verified) {
            return AttestationResult.Failed("Invalid assertion signature")
        }

        // Update the counter in KV
        val updatedKey = storedKey.copy(counter = counterValue)
        cache.put("attest:pk:$clientId", json.encodeToString(updatedKey), 365.days)

        return AttestationResult.Attested(AttestationPlatform.APP_ATTEST)
    }
}

private fun base64DecodeToBytes(input: String): ByteArray {
    val binaryString = js("atob(input)") as String
    return ByteArray(binaryString.length) { binaryString[it].code.toByte() }
}

private fun base64DecodeToArrayBuffer(input: String): ArrayBuffer {
    val bytes = base64DecodeToBytes(input)
    val uint8 = Uint8Array(bytes.toTypedArray())
    return uint8.buffer
}

private suspend fun sha256(input: ByteArray): ByteArray {
    val crypto = js("globalThis.crypto.subtle")
    val uint8 = Uint8Array(input.toTypedArray())
    val hashBuffer = (crypto.digest("SHA-256", uint8.buffer) as Promise<dynamic>)
        .kotlinx.coroutines
        .await() as ArrayBuffer
    val resultUint8 = Uint8Array(hashBuffer)
    return ByteArray(resultUint8.length) { resultUint8[it] }
}

private suspend fun verifyES256Signature(
    publicKeyBytes: ArrayBuffer,
    authenticatorData: Uint8Array,
    clientDataHash: ByteArray,
    signature: Uint8Array,
): Boolean {
    val crypto = js("globalThis.crypto.subtle")
    val algorithm = js("({ name: 'ECDSA', namedCurve: 'P-256' })")
    val verifyAlgorithm = js("({ name: 'ECDSA', hash: 'SHA-256' })")

    // Import the public key
    val key = (
        crypto.importKey(
            "spki",
            publicKeyBytes,
            algorithm,
            false,
            arrayOf("verify"),
        ) as Promise<dynamic>
    ).kotlinx.coroutines
        .await()

    // Per Apple App Attest spec: verify over authenticatorData || SHA-256(clientData)
    val clientDataHashUint8 = Uint8Array(clientDataHash.toTypedArray())
    val dataToVerify = Uint8Array(authenticatorData.length + clientDataHashUint8.length)
    dataToVerify.set(authenticatorData, 0)
    dataToVerify.set(clientDataHashUint8, authenticatorData.length)

    val verified = (
        crypto.verify(
            verifyAlgorithm,
            key,
            signature.buffer,
            dataToVerify.buffer,
        ) as Promise<Boolean>
    ).kotlinx.coroutines
        .await()

    return verified
}
