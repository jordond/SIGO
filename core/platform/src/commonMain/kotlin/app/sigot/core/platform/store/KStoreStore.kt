package app.sigot.core.platform.store

import co.touchlab.kermit.Logger
import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.serialization.Serializable

public open class KStoreStore<T : @Serializable Any>(
    protected val store: KStore<T>,
) : Store<T> {
    override val data: Flow<T> = store.updates.filterNotNull()

    public open fun createStoreData(data: T): T = data

    override suspend fun get(): T? {
        val result =
            try {
                store.get()
            } catch (cause: Throwable) {
                Logger.w(cause) { "Failed to get value!" }
                null
            }

        return result
    }

    override suspend fun set(data: T) {
        Logger.d { "Setting data..." }
        val storeData = createStoreData(data)

        try {
            store.update { storeData }
        } catch (cause: Throwable) {
            if (cause is CancellationException) throw cause
            Logger.e(cause) { "Failed to set data: $data" }
            throw cause
        }
    }

    override suspend fun update(block: (T) -> T) {
        store.update { if (it == null) null else block(it) }
    }

    override suspend fun clear() {
        store.reset()
    }
}
