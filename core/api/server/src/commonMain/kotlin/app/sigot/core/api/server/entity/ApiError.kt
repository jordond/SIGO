package app.sigot.core.api.server.entity

import kotlinx.serialization.Serializable

@Serializable
public data class ApiError(
    val error: String,
    val meta: Map<String, String> = emptyMap(),
)
