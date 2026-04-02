package now.shouldigooutside.whatsnew.data

import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.StateFlow

/**
 * Tracks which What's New entries a user has seen.
 *
 * Backed by a persistent store separate from app settings.
 * See `docs/whats-new.md` for the full system overview.
 */
public interface WhatsNewRepo {
    /**
     * Entries the user has not yet dismissed.
     */
    public val unseenEntries: StateFlow<PersistentList<WhatsNewPage>>

    /**
     *  Mark all current registry entries as seen.
     */
    public suspend fun markSeen()

    /**
     * Clear all seen state so entries reappear. Used by internal settings for testing
     */
    public fun reset()
}
