package now.shouldigooutside.core.model.preferences

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentMapOf
import kotlin.test.Test

class ActivityTest {
    @Test
    fun allContainsExactlyTheBuiltInActivities() {
        Activity.all shouldBe listOf(
            Activity.General,
            Activity.Walking,
            Activity.Running,
            Activity.Cycling,
            Activity.Hiking,
            Activity.Swimming,
        )
    }

    @Test
    fun remainingActivitiesFromEmptyMapReturnsAll() {
        val map = persistentMapOf<Activity, Preferences>()

        val result = map.remainingActivities()

        result shouldContainExactlyInAnyOrder Activity.all
    }

    @Test
    fun remainingActivitiesExcludesPresentActivities() {
        val map = persistentMapOf(
            Activity.General to Preferences.default,
            Activity.Running to Preferences.default,
        )

        val result = map.remainingActivities()

        result shouldNotContain Activity.General
        result shouldNotContain Activity.Running
        result.size shouldBe 4
    }

    @Test
    fun remainingActivitiesFromFullMapReturnsEmpty() {
        val map = persistentMapOf(
            Activity.General to Preferences.default,
            Activity.Walking to Preferences.default,
            Activity.Running to Preferences.default,
            Activity.Cycling to Preferences.default,
            Activity.Hiking to Preferences.default,
            Activity.Swimming to Preferences.default,
        )

        val result = map.remainingActivities()

        result.shouldBeEmpty()
    }

    @Test
    fun remainingActivitiesIgnoresCustomActivitiesInMap() {
        val map = persistentMapOf(
            Activity.General to Preferences.default,
            Activity.Custom("Yoga") to Preferences.default,
        )

        val result = map.remainingActivities()

        result.size shouldBe 5
        result shouldNotContain Activity.General
    }
}
