package now.shouldigooutside.core.platform

public interface ClientIdProvider {
    public suspend fun clientId(): String
}
