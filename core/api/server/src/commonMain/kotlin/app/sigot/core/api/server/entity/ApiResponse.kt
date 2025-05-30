package app.sigot.core.api.server.entity

import kotlinx.serialization.Serializable

@Serializable
public data class ApiResponse<T : @Serializable Any?>(
    val data: T,
    val meta: Map<String, String> = emptyMap(),
)
