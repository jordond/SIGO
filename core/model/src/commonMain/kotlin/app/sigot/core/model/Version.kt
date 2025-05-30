package app.sigot.core.model

public data class Version(
    val code: Int,
    val name: String,
    val sha: String? = null,
)
