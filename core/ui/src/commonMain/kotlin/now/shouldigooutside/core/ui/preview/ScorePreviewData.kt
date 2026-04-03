package now.shouldigooutside.core.ui.preview

import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult

public object ScorePreviewData {
    public val yes: ForecastScore = ForecastScore(
        current = Score(
            result = ScoreResult.Yes,
            reasons = Reasons(
                wind = ReasonValue.Near,
                temperature = ReasonValue.Inside,
                precipitation = ReasonValue.Outside,
                severeWeather = ReasonValue.Inside,
                airQuality = ReasonValue.Inside,
            ),
        ),
        hours = listOf(
            Score(
                ScoreResult.Yes,
                Reasons(
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(
                    ReasonValue.Outside,
                    ReasonValue.Near,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
        ),
        today = Score(
            result = ScoreResult.Yes,
            reasons = Reasons(
                wind = ReasonValue.Near,
                temperature = ReasonValue.Inside,
                precipitation = ReasonValue.Outside,
                severeWeather = ReasonValue.Inside,
                airQuality = ReasonValue.Inside,
            ),
        ),
        days = listOf(
            Score(
                ScoreResult.Yes,
                Reasons(
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
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
                airQuality = ReasonValue.Inside,
            ),
        ),
        hours = listOf(
            Score(
                ScoreResult.No,
                Reasons(
                    ReasonValue.Outside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.No,
                Reasons(
                    ReasonValue.Outside,
                    ReasonValue.Outside,
                    ReasonValue.Near,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.No,
                Reasons(
                    ReasonValue.Outside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.No,
                Reasons(
                    ReasonValue.Outside,
                    ReasonValue.Outside,
                    ReasonValue.Near,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                ),
            ),
        ),
        today = Score(
            result = ScoreResult.No,
            reasons = Reasons(
                wind = ReasonValue.Outside,
                temperature = ReasonValue.Outside,
                precipitation = ReasonValue.Inside,
                severeWeather = ReasonValue.Outside,
                airQuality = ReasonValue.Inside,
            ),
        ),
        days = listOf(
            Score(
                ScoreResult.No,
                Reasons(
                    ReasonValue.Outside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
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
                airQuality = ReasonValue.Inside,
            ),
        ),
        hours = listOf(
            Score(
                ScoreResult.Maybe,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.No,
                Reasons(
                    ReasonValue.Outside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Maybe,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
        ),
        today = Score(
            result = ScoreResult.Maybe,
            reasons = Reasons(
                wind = ReasonValue.Near,
                temperature = ReasonValue.Near,
                precipitation = ReasonValue.Near,
                severeWeather = ReasonValue.Inside,
                airQuality = ReasonValue.Inside,
            ),
        ),
        days = listOf(
            Score(
                ScoreResult.Maybe,
                Reasons(
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Near,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
            Score(
                ScoreResult.Yes,
                Reasons(
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                    ReasonValue.Outside,
                    ReasonValue.Inside,
                    ReasonValue.Inside,
                ),
            ),
        ),
    )
}
