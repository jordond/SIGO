package app.sigot.core.api.server.entity

import kotlinx.serialization.Serializable

@Serializable
public data class ApiResponse<T>(
    val data: T,
    val meta: Map<String, String> = emptyMap(),
)
