package now.shouldigooutside.settings.data.entity

import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences

internal data object ActivityEntity {
    const val General = "General"
    const val Walking = "Walking"
    const val Running = "Running"
    const val Cycling = "Cycling"
    const val Hiking = "Hiking"
    const val Swimming = "Swimming"
    const val CustomPrefix = "Custom:"

    fun custom(name: String) = CustomPrefix + name
}

internal fun Map<Activity, Preferences>.toEntity(): Map<String, PreferencesEntity> =
    map { (key, value) -> key.toEntity() to value.toEntity() }.toMap()

internal fun Activity.toEntity(): String =
    when (this) {
        is Activity.General -> ActivityEntity.General
        is Activity.Walking -> ActivityEntity.Walking
        is Activity.Running -> ActivityEntity.Running
        is Activity.Cycling -> ActivityEntity.Cycling
        is Activity.Hiking -> ActivityEntity.Hiking
        is Activity.Swimming -> ActivityEntity.Swimming
        is Activity.Custom -> ActivityEntity.custom(name)
    }

internal fun Map<String, PreferencesEntity>.toModel(): Map<Activity, Preferences> =
    map { (key, value) -> key.toModel() to value.toModel() }.toMap()

internal fun mapActivityEntityToModel(activity: String): Activity = activity.toModel()

private fun String.toModel(): Activity =
    when (this) {
        ActivityEntity.General -> Activity.General
        ActivityEntity.Walking -> Activity.Walking
        ActivityEntity.Running -> Activity.Running
        ActivityEntity.Cycling -> Activity.Cycling
        ActivityEntity.Hiking -> Activity.Hiking
        ActivityEntity.Swimming -> Activity.Swimming
        else -> if (startsWith(ActivityEntity.CustomPrefix)) {
            Activity.Custom(name = removePrefix(ActivityEntity.CustomPrefix))
        } else {
            error("Unknown activity type: $this")
        }
    }
