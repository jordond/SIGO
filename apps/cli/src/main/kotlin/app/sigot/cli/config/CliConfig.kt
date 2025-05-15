package app.sigot.cli.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CliConfig(
    @SerialName("token")
    val token: String,
    @SerialName("last_location")
    val lastLocation: String? = null,
)
