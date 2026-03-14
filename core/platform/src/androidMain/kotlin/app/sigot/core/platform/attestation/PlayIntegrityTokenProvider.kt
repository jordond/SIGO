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

    override val platform: String = "android"

    override suspend fun getToken(requestHash: String): String? =
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
}
