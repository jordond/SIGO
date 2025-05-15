package app.sigot.core.platform.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable

public open class MemoryStore<T : @Serializable Any>(
    private val initialValue: T,
) : Store<T> {
    private val state = MutableStateFlow(initialValue)

    override val data: Flow<T> = state.filterNotNull()

    override suspend fun get(): T? = state.value

    override suspend fun set(data: T) {
        state.update { data }
    }

    override suspend fun update(block: (T?) -> T) {
        state.update(block)
    }

    override suspend fun clear() {
        state.update { initialValue }
    }
}
