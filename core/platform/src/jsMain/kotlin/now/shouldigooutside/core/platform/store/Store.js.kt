package now.shouldigooutside.core.platform.store

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storage.storeOf
import kotlinx.serialization.Serializable

public actual inline fun <reified T : @Serializable Any> createStore(
    filename: String,
    type: Store.Type,
    default: T?,
): KStore<T> = storeOf(filename, default = default)
