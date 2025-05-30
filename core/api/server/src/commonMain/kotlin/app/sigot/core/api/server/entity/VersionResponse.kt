package app.sigot.core.api.server.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class VersionResponse(
    @SerialName("version")
    val version: VersionEntity,
)
