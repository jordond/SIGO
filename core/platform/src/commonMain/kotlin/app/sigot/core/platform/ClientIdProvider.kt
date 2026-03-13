package app.sigot.core.platform

public interface ClientIdProvider {
    public suspend fun clientId(): String
}
