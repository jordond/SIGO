package app.sigot.core.api.server.attestation

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import kotlin.js.Promise

@Serializable
private data class ServiceAccountKey(
    @SerialName("client_email")
    val clientEmail: String,
    @SerialName("private_key")
    val privateKey: String,
)

@Serializable
private data class TokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int? = null,
)

internal class GoogleAuthProvider(
    private val config: AttestationConfig,
    private val httpClient: HttpClient,
    private val json: Json,
) {
    private var cachedToken: String? = null
    private var tokenExpiry: Long = 0

    suspend fun getAccessToken(): String {
        if (config.googleServiceAccountJson.isNullOrEmpty()) {
            throw IllegalStateException("Google service account not configured")
        }
        val now = currentEpochSeconds()
        cachedToken?.let { if (now < tokenExpiry) return it }
        return refreshToken(now)
    }

    private suspend fun refreshToken(nowSeconds: Long): String {
        val serviceAccountJson = config.googleServiceAccountJson
            ?: throw IllegalStateException("Google service account not configured")
        val serviceAccount = json.decodeFromString<ServiceAccountKey>(serviceAccountJson)

        // Strip PEM headers and whitespace to get raw base64
        val pemBody = serviceAccount.privateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .trim()

        // Base64-decode to get PKCS#8 DER bytes
        val keyBytes = base64Decode(pemBody)

        // Import RSA key via Web Crypto
        val algorithm = js("({ name: 'RSASSA-PKCS1-v1_5', hash: 'SHA-256' })")
        val cryptoKey = importKey(keyBytes, algorithm)

        // Build JWT
        val header = base64UrlEncode("""{"alg":"RS256","typ":"JWT"}""")
        val claims = base64UrlEncode(
            json.encodeToString(
                JwtClaims(
                    iss = serviceAccount.clientEmail,
                    scope = "https://www.googleapis.com/auth/playintegrity",
                    aud = "https://oauth2.googleapis.com/token",
                    iat = nowSeconds,
                    exp = nowSeconds + 3600,
                ),
            ),
        )

        val signingInput = "$header.$claims"
        val signature = sign(cryptoKey, signingInput)
        val jwt = "$signingInput.$signature"

        // Exchange JWT for access token
        val response = httpClient.submitForm(
            url = "https://oauth2.googleapis.com/token",
            formParameters = parameters {
                append("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                append("assertion", jwt)
            },
        )

        val tokenResponse = json.decodeFromString<TokenResponse>(response.bodyAsText())
        cachedToken = tokenResponse.accessToken
        tokenExpiry = nowSeconds + 3500 // 50s safety margin
        return tokenResponse.accessToken
    }

    @Serializable
    private data class JwtClaims(
        val iss: String,
        val scope: String,
        val aud: String,
        val iat: Long,
        val exp: Long,
    )
}

private fun currentEpochSeconds(): Long = (js("Date.now()") as Double).toLong() / 1000

private fun base64Decode(input: String): ArrayBuffer {
    val binaryString = js("atob(input)") as String
    val bytes = Uint8Array(binaryString.length)
    for (i in binaryString.indices) {
        bytes[i] = binaryString[i].code.toByte()
    }
    return bytes.buffer
}

private fun base64UrlEncode(input: String): String {
    val base64 = js("btoa(unescape(encodeURIComponent(input)))") as String
    return base64
        .replace("+", "-")
        .replace("/", "_")
        .replace("=", "")
}

private fun base64UrlEncodeBytes(buffer: ArrayBuffer): String {
    val bytes = Uint8Array(buffer)
    val sb = StringBuilder()
    for (i in 0 until bytes.length) {
        sb.append(bytes[i].toInt().toChar())
    }
    val base64 = js("btoa(sb.toString())") as String
    return base64
        .replace("+", "-")
        .replace("/", "_")
        .replace("=", "")
}

private suspend fun importKey(
    keyData: ArrayBuffer,
    algorithm: dynamic,
): dynamic {
    val crypto = js("globalThis.crypto.subtle")
    val promise = crypto.importKey("pkcs8", keyData, algorithm, false, arrayOf("sign")) as Promise<dynamic>
    return promise.await()
}

private suspend fun sign(
    key: dynamic,
    data: String,
): String {
    val crypto = js("globalThis.crypto.subtle")
    val encoder = js("new TextEncoder()")
    val dataBytes = encoder.encode(data) as Uint8Array
    val promise = crypto.sign("RSASSA-PKCS1-v1_5", key, dataBytes.buffer) as Promise<ArrayBuffer>
    val signatureBuffer = promise.await()
    return base64UrlEncodeBytes(signatureBuffer)
}

private suspend fun <T> Promise<T>.await(): T = kotlinx.coroutines.await()
