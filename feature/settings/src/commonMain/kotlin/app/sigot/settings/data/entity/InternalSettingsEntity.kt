package app.sigot.settings.data.entity

import app.sigot.core.model.settings.InternalSettings
import app.sigot.core.platform.isDebug
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class InternalSettingsEntity(
    @SerialName("enabled")
    val enabled: Boolean,
)

internal fun InternalSettingsEntity.toModel() =
    InternalSettings(
        enabled = isDebug || enabled,
    )

internal fun InternalSettings.toEntity() =
    InternalSettingsEntity(
        enabled = enabled,
    )
