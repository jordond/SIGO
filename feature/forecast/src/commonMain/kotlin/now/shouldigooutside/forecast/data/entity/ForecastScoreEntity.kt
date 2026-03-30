package now.shouldigooutside.forecast.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.score.ForecastScore
import now.shouldigooutside.core.model.score.ReasonValue
import now.shouldigooutside.core.model.score.Reasons
import now.shouldigooutside.core.model.score.Score
import now.shouldigooutside.core.model.score.ScoreResult

@Serializable
public data class ForecastScoreEntity(
    @SerialName("current")
    val current: ScoreEntity,
    @SerialName("hours")
    val hours: List<ScoreEntity>,
    @SerialName("today")
    val today: ScoreEntity,
    @SerialName("days")
    val days: List<ScoreEntity>,
)

@Serializable
public data class ScoreEntity(
    @SerialName("result")
    val result: ScoreResult,
    @SerialName("reasons")
    val reasons: ReasonsEntity,
)

@Serializable
public data class ReasonsEntity(
    @SerialName("wind")
    val wind: ReasonValue,
    @SerialName("temperature")
    val temperature: ReasonValue,
    @SerialName("precipitation")
    val precipitation: ReasonValue,
    @SerialName("severe_weather")
    val severeWeather: ReasonValue,
    @SerialName("air_quality")
    val airQuality: ReasonValue = ReasonValue.Inside,
)

public fun ForecastScore.toEntity(): ForecastScoreEntity =
    ForecastScoreEntity(
        current = current.toEntity(),
        hours = hours.map { it.toEntity() },
        today = today.toEntity(),
        days = days.map { it.toEntity() },
    )

private fun Score.toEntity(): ScoreEntity =
    ScoreEntity(
        result = result,
        reasons = reasons.toEntity(),
    )

private fun Reasons.toEntity() =
    ReasonsEntity(
        wind = wind,
        temperature = temperature,
        precipitation = precipitation,
        severeWeather = severeWeather,
        airQuality = airQuality,
    )

public fun ForecastScoreEntity.toModel(): ForecastScore =
    ForecastScore(
        current = current.toModel(),
        hours = hours.map { it.toModel() },
        today = today.toModel(),
        days = days.map { it.toModel() },
    )

private fun ScoreEntity.toModel(): Score =
    Score(
        result = result,
        reasons = reasons.toModel(),
    )

private fun ReasonsEntity.toModel() =
    Reasons(
        wind = wind,
        temperature = temperature,
        precipitation = precipitation,
        severeWeather = severeWeather,
        airQuality = airQuality,
    )
