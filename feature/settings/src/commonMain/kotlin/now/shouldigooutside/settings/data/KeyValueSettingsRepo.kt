package now.shouldigooutside.settings.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import now.shouldigooutside.core.domain.settings.SettingsRepo
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.settings.Settings
import now.shouldigooutside.core.platform.isDebug
import now.shouldigooutside.core.platform.store.Store
import now.shouldigooutside.settings.data.entity.SettingsEntity
import now.shouldigooutside.settings.data.entity.toEntity
import now.shouldigooutside.settings.data.entity.toModel

internal class KeyValueSettingsRepo(
    private val store: Store<SettingsEntity>,
    private val scope: CoroutineScope,
) : SettingsRepo {
    override val settings: StateFlow<Settings> = store.data
        .mapNotNull { entity ->
            val model = entity.toModel()
            model.copy(
                selectedActivity = if (model.enableActivities) model.selectedActivity else Activity.General,
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = Settings(),
        )

    init {
        if (isDebug) {
            scope.launch {
                settings.collect { value ->
                    Logger.i { "Settings: $value" }
                }
            }
        }
    }

    override fun update(block: (Settings) -> Settings) {
        scope.launch {
            store.update { value ->
                val newValue = (value?.toModel() ?: Settings()).let(block).toEntity()

                Logger.d { "Updating settings to $newValue" }
                newValue
            }
        }
    }

    override fun reset() {
        scope.launch {
            update(Settings())
        }
    }
}
