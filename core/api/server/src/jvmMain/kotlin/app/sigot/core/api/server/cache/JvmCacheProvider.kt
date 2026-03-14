package app.sigot.core.api.server.cache

/**
 * JVM [CacheProvider] that eagerly provides an [InMemoryApiCache].
 */
public class JvmCacheProvider(
    override val cache: ApiCache,
) : CacheProvider
