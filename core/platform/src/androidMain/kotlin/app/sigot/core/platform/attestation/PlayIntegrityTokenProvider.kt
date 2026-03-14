package app.sigot.core.platform.attestation

import android.content.Context
import app.sigot.core.platform.AttestationTokenProvider
import co.touchlab.kermit.Logger
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import kotlinx.coroutines.tasks.await

internal class PlayIntegrityTokenProvider(
    private val context: Context,
) : AttestationTokenProvider {
    private val logger = Logger.withTag("PlayIntegrityTokenProvider")

    private data class CachedToken(
        val token: String,
        val expiresAt: Long,
    )

    private val cache = mutableMapOf<String, CachedToken>()

    override val platform: String = "android"

    override suspend fun getToken(requestHash: String): String? {
        val now = System.currentTimeMillis()
        cache[requestHash]?.let { cached ->
            if (cached.expiresAt > now) return cached.token
        }

        val token = requestToken(requestHash) ?: return null
        cache[requestHash] = CachedToken(token, now + CACHE_TTL_MS)
        return token
    }

    override fun resetAttestation() {
        cache.clear()
    }

    private suspend fun requestToken(requestHash: String): String? =
        try {
            val manager = IntegrityManagerFactory.create(context)
            val request = IntegrityTokenRequest
                .builder()
                .setNonce(requestHash)
                .build()
            val response = manager.requestIntegrityToken(request).await()
            response.token()
        } catch (e: Exception) {
            logger.w(e) { "Play Integrity token generation failed" }
            null
        }

    private companion object {
        const val CACHE_TTL_MS = 5L * 60 * 1000 // 5 minutes
    }
}
