package now.shouldigooutside.core.model

import androidx.compose.runtime.Immutable

@Immutable
public data class Version(
    val code: Int,
    val name: String,
    val sha: String? = null,
)
