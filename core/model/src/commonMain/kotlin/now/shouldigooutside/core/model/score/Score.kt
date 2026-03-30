package now.shouldigooutside.core.model.score

import androidx.compose.runtime.Immutable

@Immutable
public data class Score(
    val result: ScoreResult,
    val reasons: Reasons,
)
