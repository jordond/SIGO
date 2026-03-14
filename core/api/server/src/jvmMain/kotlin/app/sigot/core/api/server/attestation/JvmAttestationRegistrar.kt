package app.sigot.core.api.server.attestation

import app.sigot.core.api.server.cache.CacheProvider
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json
import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import java.util.Base64
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

internal class JvmAttestationRegistrar(
    private val cacheProvider: CacheProvider,
    private val json: Json,
) : AttestationRegistrar {
    private val logger = Logger.withTag("JvmAttestationRegistrar")

    override suspend fun generateNonce(clientId: String): String? {
        val cache = cacheProvider.cache ?: return null
        val nonce = Random.nextBytes(32).joinToString("") { "%02x".format(it) }
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

        val storedNonce = cache.get("attest:nonce:$clientId")
        if (storedNonce == null || storedNonce != challenge) {
            logger.w { "Nonce mismatch for $clientId" }
            return false
        }

        val attestationBytes = try {
            Base64.getDecoder().decode(attestationBase64)
        } catch (e: Exception) {
            logger.w(e) { "Failed to decode attestation for $clientId" }
            return false
        }

        // Decode CBOR attestation and extract public key
        val publicKeyBase64 = try {
            extractPublicKeyFromAttestation(attestationBytes)
        } catch (e: Exception) {
            logger.w(e) { "Failed to extract public key from attestation" }
            return false
        }

        val storedKey = StoredAttestationKey(publicKeyBase64 = publicKeyBase64, counter = 0)
        cache.put(
            "attest:pk:$clientId",
            json.encodeToString(StoredAttestationKey.serializer(), storedKey),
            365.days,
        )
        cache.put("attest:nonce:$clientId", "", 1.minutes)

        logger.i { "Successfully registered attestation for $clientId" }
        return true
    }

    private fun extractPublicKeyFromAttestation(attestationBytes: ByteArray): String {
        val cbor = kotlinx.serialization.cbor.Cbor { ignoreUnknownKeys = true }

        @kotlinx.serialization.Serializable
        data class CborAttestation(
            val authData: ByteArray,
        )

        val attestation = cbor.decodeFromByteArray(CborAttestation.serializer(), attestationBytes)
        val authData = attestation.authData

        // authData layout: rpIdHash(32) + flags(1) + counter(4) + aaguid(16) + credIdLen(2) + credId(credIdLen) + credPubKey(COSE)
        val offset = 32 + 1 + 4 + 16
        val credIdLen = ((authData[offset].toInt() and 0xFF) shl 8) or (authData[offset + 1].toInt() and 0xFF)
        val coseKeyOffset = offset + 2 + credIdLen
        val coseKeyBytes = authData.sliceArray(coseKeyOffset until authData.size)

        // Decode COSE key to extract x, y coordinates
        val (x, y) = extractCoseKeyCoordinates(coseKeyBytes)

        // Build uncompressed EC point: 0x04 || x || y
        val uncompressedPoint = ByteArray(65)
        uncompressedPoint[0] = 0x04
        x.copyInto(uncompressedPoint, 1)
        y.copyInto(uncompressedPoint, 33)

        // Import as EC public key and export as X.509/SPKI
        val ecSpec = ECGenParameterSpec("secp256r1")
        val kf = KeyFactory.getInstance("EC")
        val params = AlgorithmParameters.getInstance("EC")
        params.init(ecSpec)
        val ecParams = params.getParameterSpec(ECParameterSpec::class.java)

        val point = ECPoint(
            BigInteger(1, x),
            BigInteger(1, y),
        )
        val pubKeySpec = ECPublicKeySpec(point, ecParams)
        val publicKey = kf.generatePublic(pubKeySpec)

        return Base64.getEncoder().encodeToString(publicKey.encoded)
    }

    @Suppress("CyclomaticComplexMethod")
    private fun extractCoseKeyCoordinates(coseKeyBytes: ByteArray): Pair<ByteArray, ByteArray> {
        // Minimal CBOR map parser for COSE key
        // COSE EC2 key map has integer keys: 1=kty, 3=alg, -1=crv, -2=x, -3=y
        var x: ByteArray? = null
        var y: ByteArray? = null
        var i = 0
        val data = coseKeyBytes

        // First byte should be a CBOR map header
        if (data.isEmpty()) throw IllegalArgumentException("Empty COSE key")
        val mapHeader = data[i++].toInt() and 0xFF
        val mapSize = when {
            mapHeader in 0xA0..0xB7 -> mapHeader - 0xA0
            else -> throw IllegalArgumentException("Unexpected CBOR map header: $mapHeader")
        }

        for (entry in 0 until mapSize) {
            // Read key (integer)
            val keyByte = data[i++].toInt() and 0xFF
            val key = when {
                keyByte in 0x00..0x17 -> keyByte // positive int
                keyByte in 0x20..0x37 -> -(keyByte - 0x20) - 1 // negative int
                else -> throw IllegalArgumentException("Unexpected CBOR key type: $keyByte")
            }

            // Read value
            val valueByte = data[i].toInt() and 0xFF
            when {
                // Byte string (short)
                valueByte in 0x40..0x57 -> {
                    val len = valueByte - 0x40
                    i++
                    val value = data.sliceArray(i until i + len)
                    i += len
                    when (key) {
                        -2 -> x = value
                        -3 -> y = value
                    }
                }
                // Byte string (1-byte length)
                valueByte == 0x58 -> {
                    i++
                    val len = data[i++].toInt() and 0xFF
                    val value = data.sliceArray(i until i + len)
                    i += len
                    when (key) {
                        -2 -> x = value
                        -3 -> y = value
                    }
                }
                // Positive integer (0-23)
                valueByte in 0x00..0x17 -> {
                    i++
                }
                // Negative integer (-1 to -24)
                valueByte in 0x20..0x37 -> {
                    i++
                }
                else -> {
                    throw IllegalArgumentException("Unexpected CBOR value type: $valueByte")
                }
            }
        }

        return (x ?: throw IllegalArgumentException("Missing x coordinate")) to
            (y ?: throw IllegalArgumentException("Missing y coordinate"))
    }
}
