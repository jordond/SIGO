package now.shouldigooutside.core.api.model.entity

import kotlinx.serialization.Serializable

@Serializable
public data class ApiError(
    val error: String,
    val meta: Map<String, String> = emptyMap(),
)
