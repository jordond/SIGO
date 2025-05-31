package app.sigot.core.model.score

public enum class ReasonValue {
    Inside,
    Near,
    Outside,
    ;

    public fun toResult(): ScoreResult =
        when (this) {
            Inside -> ScoreResult.Yes
            Near -> ScoreResult.Maybe
            Outside -> ScoreResult.No
        }
}
