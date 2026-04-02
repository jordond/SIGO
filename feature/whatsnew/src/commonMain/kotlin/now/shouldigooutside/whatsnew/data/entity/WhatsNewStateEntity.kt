package now.shouldigooutside.whatsnew.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WhatsNewStateEntity(
    @SerialName("initialized")
    val initialized: Boolean = false,
    @SerialName("last_seen_version_code")
    val lastSeenVersionCode: Int? = null,
)
