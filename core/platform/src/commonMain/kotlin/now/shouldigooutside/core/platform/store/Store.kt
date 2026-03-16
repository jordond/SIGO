package now.shouldigooutside.core.platform.store

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

public interface Store<T> {
    public val data: Flow<T>

    public suspend fun get(): T?

    public suspend fun set(data: T)

    public suspend fun update(block: (T?) -> T)

    public suspend fun clear()

    public sealed interface Type {
        public data object Cache : Type

        public data object Persistent : Type
    }

    public companion object {
        public inline fun <reified T : @Serializable Any> storeOf(
            filename: String,
            type: Type,
            default: T? = null,
        ): Store<T> {
            val store = createStore<T>(filename = filename, type = type, default = default)
            return KStoreStore(store)
        }
    }
}

public expect inline fun <reified T : @Serializable Any> createStore(
    filename: String,
    type: Store.Type,
    default: T? = null,
): KStore<T>

public inline fun <reified T : @Serializable Any> createCache(
    filename: String,
    default: T? = null,
): KStore<T> = createStore(filename, Store.Type.Cache, default)

public inline fun <reified T : @Serializable Any> createPersistentStore(
    filename: String,
    default: T? = null,
): KStore<T> = createStore(filename, Store.Type.Persistent, default)
