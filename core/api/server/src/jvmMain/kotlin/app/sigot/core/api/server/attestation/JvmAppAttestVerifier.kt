package app.sigot.core.api.server.attestation

import app.sigot.core.api.server.cache.CacheProvider
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import kotlin.time.Duration.Companion.days

internal class JvmAppAttestVerifier(
    private val cacheProvider: CacheProvider,
    private val config: AttestationConfig,
    private val json: Json,
) : AttestationVerifier {
    private val logger = Logger.withTag("JvmAppAttestVerifier")

    override val platform: AttestationPlatform = AttestationPlatform.APP_ATTEST

    override suspend fun verify(
        token: String,
        clientId: String,
        requestHash: String,
    ): AttestationResult {
        val cache = cacheProvider.cache
            ?: return AttestationResult.Failed("Cache unavailable")

        val storedKeyJson = cache.get("attest:pk:$clientId")
            ?: return AttestationResult.Unattested

        val storedKey = try {
            json.decodeFromString<StoredAttestationKey>(storedKeyJson)
        } catch (e: Exception) {
            logger.w(e) { "Failed to parse stored attestation key for $clientId" }
            return AttestationResult.Failed("Invalid stored key")
        }

        // Base64-decode the assertion token
        val assertionBytes = try {
            Base64.getDecoder().decode(token)
        } catch (e: Exception) {
            return AttestationResult.Failed("Invalid assertion encoding")
        }

        // CBOR-decode the assertion to extract authenticatorData and signature
        // The assertion is a CBOR map: { "signature": bytes, "authenticatorData": bytes }
        val (authenticatorData, signature) = try {
            decodeCborAssertion(assertionBytes)
        } catch (e: Exception) {
            logger.w(e) { "Failed to decode assertion for $clientId" }
            return AttestationResult.Failed("Invalid assertion format")
        }

        // Verify counter (anti-replay) - bytes 33-36 big-endian uint32
        val counterValue = if (authenticatorData.size >= 37) {
            ((authenticatorData[33].toLong() and 0xFF) shl 24) or
                ((authenticatorData[34].toLong() and 0xFF) shl 16) or
                ((authenticatorData[35].toLong() and 0xFF) shl 8) or
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

        // Compute SHA-256(requestHash) for clientDataHash
        val clientDataHash = MessageDigest.getInstance("SHA-256").digest(requestHash.toByteArray())

        // Concatenate authenticatorData || clientDataHash
        val dataToVerify = authenticatorData + clientDataHash

        // Import stored SPKI public key and verify ES256 signature
        val publicKeyBytes = Base64.getDecoder().decode(storedKey.publicKeyBase64)
        val verified = try {
            val keySpec = X509EncodedKeySpec(publicKeyBytes)
            val publicKey = KeyFactory.getInstance("EC").generatePublic(keySpec)
            val sig = Signature.getInstance("SHA256withECDSA")
            sig.initVerify(publicKey)
            sig.update(dataToVerify)
            sig.verify(signature)
        } catch (e: Exception) {
            logger.w(e) { "Signature verification failed for $clientId" }
            return AttestationResult.Failed("Signature verification failed: ${e.message}")
        }

        if (!verified) {
            return AttestationResult.Failed("Invalid assertion signature")
        }

        // Update counter in cache
        val updatedKey = storedKey.copy(counter = counterValue)
        cache.put(
            "attest:pk:$clientId",
            json.encodeToString(StoredAttestationKey.serializer(), updatedKey),
            365.days,
        )

        return AttestationResult.Attested(AttestationPlatform.APP_ATTEST)
    }

    /**
     * Minimal CBOR map decoder for App Attest assertions.
     * Expects a CBOR map with "signature" and "authenticatorData" byte string entries.
     */
    private fun decodeCborAssertion(bytes: ByteArray): Pair<ByteArray, ByteArray> {
        val cbor = kotlinx.serialization.cbor.Cbor { ignoreUnknownKeys = true }

        @kotlinx.serialization.Serializable
        data class CborAssertion(
            val authenticatorData: ByteArray,
            val signature: ByteArray,
        )

        val assertion = cbor.decodeFromByteArray(CborAssertion.serializer(), bytes)
        return assertion.authenticatorData to assertion.signature
    }
}
