package now.shouldigooutside.auto.format

import androidx.car.app.CarContext
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat
import now.shouldigooutside.auto.R
import now.shouldigooutside.core.model.score.ScoreResult

internal interface AutoIconProvider {
    fun scoreIcon(score: ScoreResult): CarIcon

    fun locationIcon(): CarIcon
}

internal class DefaultAutoIconProvider(
    carContext: CarContext,
) : AutoIconProvider {
    private val scoreIcons: Map<ScoreResult, CarIcon> = ScoreResult.entries.associateWith { score ->
        CarIcon
            .Builder(IconCompat.createWithResource(carContext, R.drawable.ic_score_dot))
            .setTint(scoreColor(score))
            .build()
    }

    private val locationIcon: CarIcon =
        CarIcon
            .Builder(IconCompat.createWithResource(carContext, R.drawable.ic_location_pin))
            .build()

    override fun scoreIcon(score: ScoreResult): CarIcon = scoreIcons.getValue(score)

    override fun locationIcon(): CarIcon = locationIcon

    private fun scoreColor(score: ScoreResult): CarColor =
        when (score) {
            ScoreResult.Yes -> CarColor.GREEN
            ScoreResult.No -> CarColor.RED
            ScoreResult.Maybe -> CarColor.YELLOW
        }
}
