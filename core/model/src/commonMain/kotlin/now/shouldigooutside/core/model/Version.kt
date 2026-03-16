package now.shouldigooutside.core.model

public data class Version(
    val code: Int,
    val name: String,
    val sha: String? = null,
)
