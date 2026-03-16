package now.shouldigooutside.settings.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.settings.InternalSettings
import now.shouldigooutside.core.platform.isDebug

@Serializable
internal data class InternalSettingsEntity(
    @SerialName("enabled")
    val enabled: Boolean = false,
    @SerialName("simulate_failure")
    val simulateFailure: Boolean = false,
    @SerialName("backend_api_url")
    val backendApiUrl: String? = null,
    @SerialName("api_key")
    val apiKey: String? = null,
    @SerialName("use_direct_api")
    val useDirectApi: Boolean = false,
)

internal fun InternalSettingsEntity.toModel() =
    InternalSettings(
        enabled = isDebug || enabled,
        simulateFailure = simulateFailure,
        backendApiUrl = backendApiUrl?.takeIf { it.isNotBlank() } ?: InternalSettings.DefaultBackendApiUrl,
        apiKey = apiKey?.takeIf { it.isNotBlank() } ?: InternalSettings.DefaultWeatherApiToken,
        useDirectApi = useDirectApi,
    )

internal fun InternalSettings.toEntity() =
    InternalSettingsEntity(
        enabled = enabled,
        simulateFailure = simulateFailure,
        backendApiUrl = backendApiUrl,
        apiKey = apiKey,
        useDirectApi = useDirectApi,
    )
