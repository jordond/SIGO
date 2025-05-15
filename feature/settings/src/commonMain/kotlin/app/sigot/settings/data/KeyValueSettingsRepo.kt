package app.sigot.settings.data

import app.sigot.core.domain.settings.SettingsRepo
import app.sigot.core.model.settings.Settings
import app.sigot.core.platform.isDebug
import app.sigot.settings.data.entity.SettingsEntity
import app.sigot.settings.data.entity.toEntity
import app.sigot.settings.data.entity.toModel
import co.touchlab.kermit.Logger
import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class KeyValueSettingsRepo(
    private val store: KStore<SettingsEntity>,
    private val scope: CoroutineScope,
) : SettingsRepo {
    override val settings: StateFlow<Settings> = store.updates
        .mapNotNull { it?.toModel() }
        .stateIn(
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
