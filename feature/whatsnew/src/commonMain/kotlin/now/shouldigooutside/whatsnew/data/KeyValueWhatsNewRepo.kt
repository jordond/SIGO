package now.shouldigooutside.whatsnew.data

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.VersionProvider
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.Initializable
import now.shouldigooutside.core.platform.store.Store
import now.shouldigooutside.whatsnew.data.entity.WhatsNewStateEntity

/**
 * Persists which What's New entries a user has seen via KStore.
 *
 * Implements [Initializable] to handle fresh-install detection at startup:
 * fresh installations get all entries pre-marked as seen so onboarding users
 * never see a redundant What's New sheet.
 */
internal class KeyValueWhatsNewRepo(
    private val store: Store<WhatsNewStateEntity>,
    private val settingsRepo: SettingsRepo,
    private val registry: WhatsNewRegistry,
    private val versionProvider: VersionProvider,
    private val scope: CoroutineScope,
) : WhatsNewRepo,
    Initializable {
    override val unseenEntries: StateFlow<PersistentList<WhatsNewPage>> = store.data
        .map { state ->
            val seen = state.lastSeenVersionCode ?: versionProvider.provide().code
            registry.pages.filter { it.version > seen }.toPersistentList()
        }.stateIn(scope, SharingStarted.Eagerly, persistentListOf())

    override suspend fun initialize() {
        val settings = settingsRepo.settings.first { it.loaded }
        store.update { current ->
            if (current?.initialized == true) {
                current
            } else {
                val current = versionProvider.provide().code
                val code = if (settings.hasCompletedOnboarding) current else current + 1
                WhatsNewStateEntity(initialized = true, lastSeenVersionCode = code)
            }
        }
    }

    override suspend fun markSeen() {
        store.update { entity ->
            entity?.copy(lastSeenVersionCode = versionProvider.provide().code)
                ?: WhatsNewStateEntity(
                    initialized = true,
                    lastSeenVersionCode = versionProvider.provide().code,
                )
        }
    }

    override suspend fun reset() {
        scope.launch {
            store.update { current ->
                val existing = current ?: WhatsNewStateEntity(initialized = true)
                existing.copy(lastSeenVersionCode = 0)
            }
        }
    }
}
