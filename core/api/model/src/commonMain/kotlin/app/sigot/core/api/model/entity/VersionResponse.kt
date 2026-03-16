package app.sigot.core.api.model.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class VersionResponse(
    @SerialName("version")
    val version: VersionEntity,
)
