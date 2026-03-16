package now.shouldigooutside.core.platform.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

public class NoopStore<T> : Store<T> {
    override val data: Flow<T> = emptyFlow()

    override suspend fun get(): T? = null

    override suspend fun set(data: T) {}

    override suspend fun update(block: (T?) -> T) {}

    override suspend fun clear() {}
}
