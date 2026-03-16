package now.shouldigooutside.core.platform.internal

import now.shouldigooutside.core.platform.ClientIdProvider
import now.shouldigooutside.core.platform.store.Store
import kotlin.uuid.Uuid

internal class SettingsClientIdProvider(
    private val store: Store<String>,
) : ClientIdProvider {
    override suspend fun clientId(): String {
        val existing = store.get()
        if (existing != null) return existing

        val newId = Uuid.random().toString()
        store.set(newId)
        return newId
    }
}
