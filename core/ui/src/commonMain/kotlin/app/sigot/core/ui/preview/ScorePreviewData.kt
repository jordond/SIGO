package app.sigot.core.ui.preview

import app.sigot.core.model.score.ForecastScore
import app.sigot.core.model.score.ReasonValue
import app.sigot.core.model.score.Reasons
import app.sigot.core.model.score.Score
import app.sigot.core.model.score.ScoreResult

public object ScorePreviewData {
    public val yes: ForecastScore = ForecastScore(
        current = Score(
            result = ScoreResult.Yes,
            reasons = Reasons(
                wind = ReasonValue.Near,
                temperature = ReasonValue.Inside,
                precipitation = ReasonValue.Outside,
                severeWeather = ReasonValue.Inside,
            ),
        ),
        hours = listOf(
            Score(
                ScoreResult.Yes,
                Reasons(ReasonValue.Inside, ReasonValue.Inside, ReasonValue.Outside, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(ReasonValue.Near, ReasonValue.Inside, ReasonValue.Outside, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(ReasonValue.Outside, ReasonValue.Near, ReasonValue.Outside, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(ReasonValue.Inside, ReasonValue.Inside, ReasonValue.Outside, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(ReasonValue.Near, ReasonValue.Inside, ReasonValue.Outside, ReasonValue.Inside),
            ),
        ),
        today = Score(
            result = ScoreResult.Yes,
            reasons = Reasons(
                wind = ReasonValue.Near,
                temperature = ReasonValue.Inside,
                precipitation = ReasonValue.Outside,
                severeWeather = ReasonValue.Inside,
            ),
        ),
        days = listOf(
            Score(
                ScoreResult.Yes,
                Reasons(ReasonValue.Inside, ReasonValue.Inside, ReasonValue.Outside, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(ReasonValue.Near, ReasonValue.Near, ReasonValue.Near, ReasonValue.Inside),
            ),
        ),
    )

    public val no: ForecastScore = ForecastScore(
        current = Score(
            result = ScoreResult.No,
            reasons = Reasons(
                wind = ReasonValue.Outside,
                temperature = ReasonValue.Outside,
                precipitation = ReasonValue.Inside,
                severeWeather = ReasonValue.Outside,
            ),
        ),
        hours = listOf(
            Score(
                ScoreResult.No,
                Reasons(ReasonValue.Outside, ReasonValue.Outside, ReasonValue.Inside, ReasonValue.Outside),
            ),
            Score(
                ScoreResult.No,
                Reasons(ReasonValue.Outside, ReasonValue.Outside, ReasonValue.Near, ReasonValue.Outside),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(ReasonValue.Near, ReasonValue.Near, ReasonValue.Near, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.No,
                Reasons(ReasonValue.Outside, ReasonValue.Outside, ReasonValue.Inside, ReasonValue.Outside),
            ),
            Score(
                ScoreResult.No,
                Reasons(ReasonValue.Outside, ReasonValue.Outside, ReasonValue.Near, ReasonValue.Outside),
            ),
        ),
        today = Score(
            result = ScoreResult.No,
            reasons = Reasons(
                wind = ReasonValue.Outside,
                temperature = ReasonValue.Outside,
                precipitation = ReasonValue.Inside,
                severeWeather = ReasonValue.Outside,
            ),
        ),
        days = listOf(
            Score(
                ScoreResult.No,
                Reasons(ReasonValue.Outside, ReasonValue.Outside, ReasonValue.Inside, ReasonValue.Outside),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(ReasonValue.Near, ReasonValue.Near, ReasonValue.Near, ReasonValue.Inside),
            ),
        ),
    )

    public val maybe: ForecastScore = ForecastScore(
        current = Score(
            result = ScoreResult.Maybe,
            reasons = Reasons(
                wind = ReasonValue.Near,
                temperature = ReasonValue.Near,
                precipitation = ReasonValue.Near,
                severeWeather = ReasonValue.Inside,
            ),
        ),
        hours = listOf(
            Score(
                ScoreResult.Maybe,
                Reasons(ReasonValue.Near, ReasonValue.Near, ReasonValue.Near, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(ReasonValue.Inside, ReasonValue.Inside, ReasonValue.Outside, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.No,
                Reasons(ReasonValue.Outside, ReasonValue.Outside, ReasonValue.Inside, ReasonValue.Outside),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(ReasonValue.Near, ReasonValue.Near, ReasonValue.Near, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(ReasonValue.Near, ReasonValue.Inside, ReasonValue.Near, ReasonValue.Inside),
            ),
        ),
        today = Score(
            result = ScoreResult.Maybe,
            reasons = Reasons(
                wind = ReasonValue.Near,
                temperature = ReasonValue.Near,
                precipitation = ReasonValue.Near,
                severeWeather = ReasonValue.Inside,
            ),
        ),
        days = listOf(
            Score(
                ScoreResult.Maybe,
                Reasons(ReasonValue.Near, ReasonValue.Near, ReasonValue.Near, ReasonValue.Inside),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(ReasonValue.Inside, ReasonValue.Inside, ReasonValue.Outside, ReasonValue.Inside),
            ),
        ),
    )
}
