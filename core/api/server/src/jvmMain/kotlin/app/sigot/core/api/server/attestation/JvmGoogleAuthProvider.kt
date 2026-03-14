package app.sigot.core.api.server.attestation

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

@Serializable
private data class ServiceAccountKey(
    @SerialName("client_email") val clientEmail: String,
    @SerialName("private_key") val privateKey: String,
)

@Serializable
private data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: Int? = null,
)

internal class JvmGoogleAuthProvider(
    private val config: AttestationConfig,
    private val httpClient: HttpClient,
    private val json: Json,
) : GoogleAuthProvider {
    private var cachedToken: String? = null
    private var tokenExpiry: Long = 0

    override suspend fun getAccessToken(): String {
        if (config.googleServiceAccountJson.isNullOrEmpty()) {
            throw IllegalStateException("Google service account not configured")
        }
        val now = System.currentTimeMillis() / 1000
        cachedToken?.let { if (now < tokenExpiry) return it }
        return refreshToken(now)
    }

    private suspend fun refreshToken(nowSeconds: Long): String {
        val serviceAccountJson = config.googleServiceAccountJson
            ?: throw IllegalStateException("Google service account not configured")
        val serviceAccount = json.decodeFromString<ServiceAccountKey>(serviceAccountJson)

        val pemBody = serviceAccount.privateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\n", "")
            .replace("\n", "")
            .trim()

        val keyBytes = Base64.getDecoder().decode(pemBody)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec)

        @Serializable
        data class JwtClaims(
            val iss: String,
            val scope: String,
            val aud: String,
            val iat: Long,
            val exp: Long,
        )

        val header = base64UrlEncode("""{"alg":"RS256","typ":"JWT"}""".toByteArray())
        val claimsJson = json.encodeToString(
            JwtClaims.serializer(),
            JwtClaims(
                iss = serviceAccount.clientEmail,
                scope = "https://www.googleapis.com/auth/playintegrity",
                aud = "https://oauth2.googleapis.com/token",
                iat = nowSeconds,
                exp = nowSeconds + 3600,
            ),
        )
        val claims = base64UrlEncode(claimsJson.toByteArray())

        val signingInput = "$header.$claims"
        val sig = Signature.getInstance("SHA256withRSA")
        sig.initSign(privateKey)
        sig.update(signingInput.toByteArray())
        val signature = base64UrlEncode(sig.sign())
        val jwt = "$signingInput.$signature"

        val response = httpClient.submitForm(
            url = "https://oauth2.googleapis.com/token",
            formParameters = parameters {
                append("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer")
                append("assertion", jwt)
            },
        )

        val tokenResponse = json.decodeFromString<TokenResponse>(response.bodyAsText())
        cachedToken = tokenResponse.accessToken
        tokenExpiry = nowSeconds + 3500
        return tokenResponse.accessToken
    }

    private fun base64UrlEncode(bytes: ByteArray): String =
        Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}
