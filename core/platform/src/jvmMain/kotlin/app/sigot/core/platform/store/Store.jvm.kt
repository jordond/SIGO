package app.sigot.core.platform.store

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.cacheDir
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.toKotlinxIoPath
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.serialization.Serializable
import okio.FileSystem
import okio.Path.Companion.toPath

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
    runCatching {
        FileSystem.SYSTEM.createDirectories(folder.path.toPath())
    }

    val path = PlatformFile(folder, filename).toKotlinxIoPath()
    return storeOf(path, default = default)
}
