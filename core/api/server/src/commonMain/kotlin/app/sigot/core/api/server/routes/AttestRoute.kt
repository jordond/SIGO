package app.sigot.core.api.server.routes

import app.sigot.core.api.server.ApiRoute
import app.sigot.core.api.server.ApiRoutePath
import app.sigot.core.api.server.attestation.AttestationRegistrar
import app.sigot.core.api.server.http.ApiHeaders
import app.sigot.core.api.server.http.ServerRequest
import app.sigot.core.api.server.http.ServerResponse
import app.sigot.core.api.server.util.badRequest
import app.sigot.core.api.server.util.ok
import app.sigot.core.api.server.util.serverError
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

internal class AttestRoute(
    private val registrar: AttestationRegistrar,
    private val json: Json,
) : ApiRoute {
    override val path: ApiRoutePath = ApiRoutePath.Attest

    override suspend fun get(
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        // GET /v1/attest — returns a nonce for attestation challenge
        val clientId = request.headers[ApiHeaders.CLIENT_ID] ?: return badRequest(
            meta = mapOf("error" to "Missing client ID"),
            json = json,
        )
        val nonce = registrar.generateNonce(clientId)
            ?: return serverError(message = "Failed to generate nonce", json = json)
        return ok(data = NonceResponse(nonce = nonce), json = json)
    }

    override suspend fun post(
        request: ServerRequest,
        parameters: Map<String, String>,
    ): ServerResponse {
        // POST /v1/attest — register a device attestation
        val clientId = request.headers[ApiHeaders.CLIENT_ID] ?: return badRequest(
            meta = mapOf("error" to "Missing client ID"),
            json = json,
        )
        val body = request.body ?: return badRequest(
            meta = mapOf("error" to "Missing request body"),
            json = json,
        )
        val attestRequest = try {
            json.decodeFromString<AttestRequest>(body)
        } catch (e: Exception) {
            return badRequest(meta = mapOf("error" to "Invalid request body"), json = json)
        }

        val success = registrar.registerAttestation(
            clientId = clientId,
            attestationBase64 = attestRequest.attestation,
            keyId = attestRequest.keyId,
            challenge = attestRequest.challenge,
        )

        return if (success) {
            ok(data = AttestResponse(registered = true), json = json)
        } else {
            badRequest(meta = mapOf("error" to "Attestation registration failed"), json = json)
        }
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

    @Serializable
    private data class AttestResponse(
        val registered: Boolean,
    )
}
