package app.sigot.core.api.model.entity

import kotlinx.serialization.Serializable

@Serializable
public data class ApiResponse<T>(
    val data: T,
    val meta: Map<String, String> = emptyMap(),
)
