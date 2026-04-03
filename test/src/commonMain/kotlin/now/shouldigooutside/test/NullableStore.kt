package now.shouldigooutside.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import now.shouldigooutside.core.platform.store.Store

public class NullableStore<T : Any>(
    initial: T? = null,
) : Store<T> {
    private val state = MutableStateFlow(initial)
    public var clearCalled: Boolean = false

    override val data: Flow<T> = state.filterNotNull()

    override suspend fun get(): T? = state.value

    override suspend fun set(data: T) {
        state.value = data
    }

    override suspend fun update(block: (T?) -> T) {
        state.update(block)
    }

    override suspend fun clear() {
        clearCalled = true
        state.value = null
    }
}
