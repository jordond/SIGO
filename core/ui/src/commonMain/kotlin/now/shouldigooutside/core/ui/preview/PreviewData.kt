package now.shouldigooutside.core.ui.preview

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import dev.jordond.compass.Coordinates
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import now.shouldigooutside.core.model.location.Location
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences
import now.shouldigooutside.core.model.score.ActivityForecastScore
import now.shouldigooutside.core.model.score.ForecastScore
import kotlin.time.Clock
import dev.jordond.compass.Location as CompassLocation

public object PreviewData {
    public val Icon: ImageVector
        get() = PreviewIcon

    public val location: Location = Location(
        latitude = 51.5074,
        longitude = -0.1278,
        name = "London, ON",
    )

    public val compassLocation: CompassLocation = CompassLocation(
        coordinates = Coordinates(latitude = 43.6532, longitude = -79.3832),
        accuracy = 100.0,
        azimuth = null,
        speed = null,
        mslAltitude = null,
        ellipsoidalAltitude = null,
        timestampMillis = Clock.System.now().toEpochMilliseconds(),
    )

    public val Score: ScorePreviewData = ScorePreviewData

    public val Forecast: ForecastPreviewData = ForecastPreviewData

    public val Activities: PersistentMap<Activity, Preferences> = persistentMapOf(
        Activity.General to Preferences.default,
        Activity.Running to Preferences.default,
        Activity.Cycling to Preferences.default,
        Activity.Hiking to Preferences.default,
    )

    public fun activities(count: Int = Activities.size): PersistentMap<Activity, Preferences> =
        Activities
            .toList()
            .take(count)
            .toMap()
            .toPersistentMap()

    public fun activityScore(
        activity: Activity = Activity.Running,
        score: ForecastScore = Score.yes,
    ): ActivityForecastScore =
        ActivityForecastScore(
            activity = activity,
            score = score,
            preferences = Preferences.defaultFor(activity),
        )

    public fun activityScores(
        count: Int = Activity.all.size,
        activities: List<Activity> = Activity.all,
        score: ForecastScore = Score.yes,
        block: (Int, Activity) -> ForecastScore = { _, _ -> score },
    ): PersistentList<ActivityForecastScore> =
        activities
            .take(count)
            .mapIndexed { index, activity ->
                ActivityForecastScore(
                    activity = activity,
                    score = block(index, activity),
                    preferences = Preferences.defaultFor(activity),
                )
            }.toPersistentList()
}

internal val PreviewIcon: ImageVector
    get() {
        if (previewIcon != null) {
            return previewIcon!!
        }
        previewIcon = ImageVector
            .Builder(
                name = "PreviewIcon",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            ).apply {
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(2.062f, 12.348f)
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -0.696f)
                    arcToRelative(
                        10.75f,
                        10.75f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        19.876f,
                        0f,
                    )
                    arcToRelative(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 0.696f)
                    arcToRelative(
                        10.75f,
                        10.75f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -19.876f,
                        0f,
                    )
                }
                path(
                    fill = null,
                    fillAlpha = 1.0f,
                    stroke = SolidColor(Color(0xFF000000)),
                    strokeAlpha = 1.0f,
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    strokeLineMiter = 1.0f,
                    pathFillType = PathFillType.NonZero,
                ) {
                    moveTo(15f, 12f)
                    arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 15f)
                    arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 12f)
                    arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15f, 12f)
                    close()
                }
            }.build()
        return previewIcon!!
    }

private var previewIcon: ImageVector? = null
