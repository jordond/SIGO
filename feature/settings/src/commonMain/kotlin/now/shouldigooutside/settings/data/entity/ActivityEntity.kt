package now.shouldigooutside.settings.data.entity

import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.preferences.Activity
import now.shouldigooutside.core.model.preferences.Preferences

@Serializable
internal sealed interface ActivityEntity {
    @Serializable
    data object General : ActivityEntity

    @Serializable
    data object Walking : ActivityEntity

    @Serializable
    data object Running : ActivityEntity

    @Serializable
    data object Cycling : ActivityEntity

    @Serializable
    data object Hiking : ActivityEntity

    @Serializable
    data object Swimming : ActivityEntity

    @Serializable
    data class Custom(
        val name: String,
    ) : ActivityEntity
}

internal fun Map<Activity, Preferences>.toEntity(): Map<ActivityEntity, PreferencesEntity> =
    map { (key, value) -> key.toEntity() to value.toEntity() }.toMap()

private fun Activity.toEntity(): ActivityEntity =
    when (this) {
        is Activity.General -> ActivityEntity.General
        is Activity.Walking -> ActivityEntity.Walking
        is Activity.Running -> ActivityEntity.Running
        is Activity.Cycling -> ActivityEntity.Cycling
        is Activity.Hiking -> ActivityEntity.Hiking
        is Activity.Swimming -> ActivityEntity.Swimming
        is Activity.Custom -> ActivityEntity.Custom(name)
    }

internal fun Map<ActivityEntity, PreferencesEntity>.toModel(): Map<Activity, Preferences> =
    map { (key, value) -> key.toModel() to value.toModel() }.toMap()

private fun ActivityEntity.toModel(): Activity =
    when (this) {
        is ActivityEntity.General -> Activity.General
        is ActivityEntity.Walking -> Activity.Walking
        is ActivityEntity.Running -> Activity.Running
        is ActivityEntity.Cycling -> Activity.Cycling
        is ActivityEntity.Hiking -> Activity.Hiking
        is ActivityEntity.Swimming -> Activity.Swimming
        is ActivityEntity.Custom -> Activity.Custom(name)
    }
