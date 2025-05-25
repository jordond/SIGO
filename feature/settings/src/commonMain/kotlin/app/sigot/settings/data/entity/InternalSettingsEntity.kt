package app.sigot.settings.data.entity

import app.sigot.core.model.settings.InternalSettings
import app.sigot.core.platform.isDebug
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class InternalSettingsEntity(
    @SerialName("enabled")
    val enabled: Boolean,
    @SerialName("backend_api_url")
    val backendApiUrl: String,
    @SerialName("api_key")
    val apiKey: String?,
    @SerialName("use_direct_api")
    val useDirectApi: Boolean,
)

internal fun InternalSettingsEntity.toModel() =
    InternalSettings(
        enabled = isDebug || enabled,
        backendApiUrl = backendApiUrl,
        apiKey = apiKey,
        useDirectApi = useDirectApi,
    )

internal fun InternalSettings.toEntity() =
    InternalSettingsEntity(
        enabled = enabled,
        backendApiUrl = backendApiUrl,
        apiKey = apiKey,
        useDirectApi = useDirectApi,
    )
