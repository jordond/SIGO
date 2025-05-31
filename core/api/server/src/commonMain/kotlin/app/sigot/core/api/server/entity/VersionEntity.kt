package app.sigot.core.api.server.entity

import app.sigot.core.model.Version
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class VersionEntity(
    @SerialName("name")
    val name: String,
    @SerialName("code")
    val code: Int,
    @SerialName("sha")
    val sha: String?,
)

public fun Version.toEntity(): VersionEntity =
    VersionEntity(
        name = name,
        code = code,
        sha = sha,
    )

public fun VersionEntity.toModel(): Version =
    Version(
        name = name,
        code = code,
        sha = sha,
    )
