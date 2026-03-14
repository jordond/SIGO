package app.sigot.core.api.server.attestation

import app.sigot.core.api.server.cache.CacheProvider
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

internal class KvAttestationRegistrar(
    private val cacheProvider: CacheProvider,
    private val config: AttestationConfig,
    private val json: Json,
) : AttestationRegistrar {
    private val logger = Logger.withTag("KvAttestationRegistrar")

    override suspend fun generateNonce(clientId: String): String? {
        val cache = cacheProvider.cache ?: return null
        val nonce = Random.nextBytes(32).toHexString()
        cache.put("attest:nonce:$clientId", nonce, 5.minutes)
        return nonce
    }

    override suspend fun registerAttestation(
        clientId: String,
        attestationBase64: String,
        keyId: String,
        challenge: String,
    ): Boolean {
        val cache = cacheProvider.cache ?: return false

        // Verify the challenge nonce
        val storedNonce = cache.get("attest:nonce:$clientId")
        if (storedNonce == null || storedNonce != challenge) {
            logger.w { "Nonce mismatch for $clientId" }
            return false
        }

        // Decode the attestation object (CBOR)
        val attestationBytes = try {
            base64DecodeToByteArray(attestationBase64)
        } catch (e: Exception) {
            logger.w(e) { "Failed to decode attestation for $clientId" }
            return false
        }

        val attestation = try {
            cborDecode(attestationBytes)
        } catch (e: Exception) {
            logger.w(e) { "CBOR decode failed for attestation" }
            return false
        }

        // Extract the attestation statement and auth data
        val authData = attestation.authData as? Uint8Array
        if (authData == null) {
            logger.w { "Missing authData in attestation" }
            return false
        }

        // Extract the public key from the credential data in authData
        // The credential public key starts at offset 55 + credentialIdLength in authData
        // For App Attest, we extract and store the COSE key
        val publicKeyBase64 = try {
            extractAndStorePublicKey(authData)
        } catch (e: Exception) {
            logger.w(e) { "Failed to extract public key from attestation" }
            return false
        }

        // Store the public key in KV
        val storedKey = StoredAttestationKey(publicKeyBase64 = publicKeyBase64, counter = 0)
        cache.put("attest:pk:$clientId", json.encodeToString(storedKey), 365.days)

        // Clean up used nonce
        cache.put("attest:nonce:$clientId", "", 1.minutes) // Short TTL to effectively delete

        logger.i { "Successfully registered attestation for $clientId" }
        return true
    }
}

private fun base64DecodeToByteArray(input: String): ByteArray {
    val binaryString = js("atob(input)") as String
    return ByteArray(binaryString.length) { binaryString[it].code.toByte() }
}

private fun ByteArray.toHexString(): String = joinToString("") { it.toUByte().toString(16).padStart(2, '0') }

private suspend fun extractAndStorePublicKey(authData: Uint8Array): String {
    // Export the raw SPKI public key bytes from the authData
    // authData layout: rpIdHash(32) + flags(1) + counter(4) + aaguid(16) + credIdLen(2) + credId(credIdLen) + credPubKey(COSE)
    // For simplicity, we extract the credential public key area and import as COSE then export as SPKI
    val offset = 32 + 1 + 4 + 16 // rpIdHash + flags + counter + aaguid
    val credIdLen = (authData[offset].toInt() and 0xFF shl 8) or (authData[offset + 1].toInt() and 0xFF)
    val coseKeyOffset = offset + 2 + credIdLen

    // Extract remaining bytes as COSE key
    val coseKeyBytes = ByteArray(authData.length - coseKeyOffset)
    for (i in coseKeyBytes.indices) {
        coseKeyBytes[i] = authData[coseKeyOffset + i]
    }

    // Decode COSE key to extract x,y coordinates for P-256
    val coseKey = cborDecode(coseKeyBytes)
    val x = coseKey[js("-2")] as Uint8Array // COSE key parameter -2 = x coordinate
    val y = coseKey[js("-3")] as Uint8Array // COSE key parameter -3 = y coordinate

    // Build uncompressed EC point: 0x04 || x || y
    val uncompressedPoint = Uint8Array(1 + 32 + 32)
    uncompressedPoint[0] = 0x04.toByte()
    for (i in 0 until 32) {
        uncompressedPoint[1 + i] = x[i]
        uncompressedPoint[33 + i] = y[i]
    }

    // Import as EC key and export as SPKI
    val crypto = js("globalThis.crypto.subtle")
    val algorithm = js("({ name: 'ECDSA', namedCurve: 'P-256' })")
    val key = (
        crypto.importKey(
            "raw",
            uncompressedPoint.buffer,
            algorithm,
            true,
            arrayOf("verify"),
        ) as Promise<dynamic>
    ).kotlinx.coroutines
        .await()
    val spkiBytes = (crypto.exportKey("spki", key) as Promise<dynamic>)
        .kotlinx.coroutines
        .await() as org.khronos.webgl.ArrayBuffer

    // Base64 encode the SPKI bytes
    val uint8 = Uint8Array(spkiBytes)
    val sb = StringBuilder()
    for (i in 0 until uint8.length) {
        sb.append(uint8[i].toInt().toChar())
    }
    return js("btoa(sb.toString())") as String
}

private suspend fun <T> Promise<T>.await(): T = kotlinx.coroutines.await()
