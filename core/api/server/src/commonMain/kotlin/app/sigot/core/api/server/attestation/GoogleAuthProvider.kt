package app.sigot.core.api.server.attestation

public interface GoogleAuthProvider {
    public suspend fun getAccessToken(): String
}
