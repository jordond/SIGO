package app.sigot.core.platform.internal

import app.sigot.core.platform.ClientIdProvider
import app.sigot.core.platform.store.Store
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
