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
    private val logger = Logger.withTag("KStoreStore")
    override val data: Flow<T> = store.updates.filterNotNull()

    public open fun createStoreData(data: T): T = data

    override suspend fun get(): T? {
        val result =
            try {
                store.get()
            } catch (cause: Throwable) {
                logger.w(cause) { "Failed to get value!" }
                null
            }

        return result
    }

    override suspend fun set(data: T) {
        logger.d { "Saving data to the store..." }
        val storeData = createStoreData(data)

        try {
            store.update { storeData }
        } catch (cause: Throwable) {
            if (cause is CancellationException) throw cause
            logger.e(cause) { "Failed to set data: $data" }
            throw cause
        }
    }

    override suspend fun update(block: (T?) -> T) {
        store.update(block)
    }

    override suspend fun clear() {
        store.reset()
    }
}
