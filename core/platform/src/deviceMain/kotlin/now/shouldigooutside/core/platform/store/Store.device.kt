package now.shouldigooutside.core.platform.store

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.resolve
import io.github.vinceglb.filekit.toKotlinxIoPath
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.serialization.Serializable

public actual inline fun <reified T : @Serializable Any> createStore(
    filename: String,
    type: Store.Type,
    default: T?,
): KStore<T> {
    val folder =
        when (type) {
            is Store.Type.Cache -> FileKit.cacheDir
            is Store.Type.Persistent -> FileKit.filesDir
        }
    val path = folder.resolve(filename).toKotlinxIoPath()
    return storeOf(path, default = default)
}
