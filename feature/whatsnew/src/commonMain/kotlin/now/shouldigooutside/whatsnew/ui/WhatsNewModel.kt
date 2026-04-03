package now.shouldigooutside.whatsnew.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dev.stateholder.extensions.viewmodel.StateViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import now.shouldigooutside.whatsnew.data.WhatsNewPage
import now.shouldigooutside.whatsnew.data.WhatsNewRepo

@Stable
internal class WhatsNewModel(
    private val whatsNewRepo: WhatsNewRepo,
) : StateViewModel<WhatsNewModel.State>(State()) {
    init {
        whatsNewRepo.unseenEntries.mergeState { state, entries -> state.copy(entries = entries) }
    }

    fun dismiss() {
        val entries = state.value.entries
        if (entries.isNotEmpty()) {
            viewModelScope.launch {
                whatsNewRepo.markSeen()
            }
        }
    }

    @Immutable
    data class State(
        val entries: PersistentList<WhatsNewPage> = persistentListOf(),
    ) {
        val isVisible: Boolean = entries.isNotEmpty()
    }
}
