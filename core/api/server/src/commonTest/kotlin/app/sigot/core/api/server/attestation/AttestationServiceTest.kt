package app.sigot.core.api.server.attestation

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class AttestationServiceTest {
    private val androidVerifier = FakeAttestationVerifier(
        platform = AttestationPlatform.PLAY_INTEGRITY,
        result = AttestationResult.Attested(AttestationPlatform.PLAY_INTEGRITY),
    )
    private val iosVerifier = FakeAttestationVerifier(
        platform = AttestationPlatform.APP_ATTEST,
        result = AttestationResult.Attested(AttestationPlatform.APP_ATTEST),
    )
    private val service = AttestationService(listOf(androidVerifier, iosVerifier))

    @Test
    fun missingTokenReturnsUnattested() =
        runTest {
            val result = service.verify(
                token = null,
                platform = "android",
                clientId = "client1",
                requestHash = "abc123",
            )
            result.shouldBeInstanceOf<AttestationResult.Unattested>()
        }

    @Test
    fun missingPlatformReturnsUnattested() =
        runTest {
            val result = service.verify(
                token = "some-token",
                platform = null,
                clientId = "client1",
                requestHash = "abc123",
            )
            result.shouldBeInstanceOf<AttestationResult.Unattested>()
        }

    @Test
    fun bothMissingReturnsUnattested() =
        runTest {
            val result = service.verify(
                token = null,
                platform = null,
                clientId = "client1",
                requestHash = "abc123",
            )
            result.shouldBeInstanceOf<AttestationResult.Unattested>()
        }

    @Test
    fun unknownPlatformReturnsUnattested() =
        runTest {
            val result = service.verify(
                token = "token",
                platform = "windows",
                clientId = "client1",
                requestHash = "abc123",
            )
            result.shouldBeInstanceOf<AttestationResult.Unattested>()
        }

    @Test
    fun androidPlatformDispatchesToPlayIntegrityVerifier() =
        runTest {
            val result = service.verify(
                token = "android-token",
                platform = "android",
                clientId = "client1",
                requestHash = "abc123",
            )
            result.shouldBeInstanceOf<AttestationResult.Attested>()
            (result as AttestationResult.Attested).platform shouldBe AttestationPlatform.PLAY_INTEGRITY
            androidVerifier.lastToken shouldBe "android-token"
            androidVerifier.lastClientId shouldBe "client1"
        }

    @Test
    fun iosPlatformDispatchesToAppAttestVerifier() =
        runTest {
            val result = service.verify(
                token = "ios-token",
                platform = "ios",
                clientId = "client1",
                requestHash = "abc123",
            )
            result.shouldBeInstanceOf<AttestationResult.Attested>()
            (result as AttestationResult.Attested).platform shouldBe AttestationPlatform.APP_ATTEST
            iosVerifier.lastToken shouldBe "ios-token"
            iosVerifier.lastClientId shouldBe "client1"
        }

    @Test
    fun verifierExceptionReturnsFailed() =
        runTest {
            val throwingVerifier = object : AttestationVerifier {
                override val platform = AttestationPlatform.PLAY_INTEGRITY

                override suspend fun verify(
                    token: String,
                    clientId: String,
                    requestHash: String,
                ): AttestationResult = throw RuntimeException("Network error")
            }
            val serviceWithThrowing = AttestationService(listOf(throwingVerifier))
            val result = serviceWithThrowing.verify(
                token = "token",
                platform = "android",
                clientId = "client1",
                requestHash = "abc123",
            )
            result.shouldBeInstanceOf<AttestationResult.Failed>()
            (result as AttestationResult.Failed).reason shouldBe "Network error"
        }
}
