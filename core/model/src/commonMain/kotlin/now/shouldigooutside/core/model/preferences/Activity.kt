package now.shouldigooutside.core.model.preferences

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList

@Immutable
public sealed interface Activity {
    public data object General : Activity

    public data object Walking : Activity

    public data object Running : Activity

    public data object Cycling : Activity

    public data object Hiking : Activity

    public data object Swimming : Activity

    public data class Custom(
        val name: String,
    ) : Activity

    public companion object {
        public val all: List<Activity> = listOf(
            General,
            Walking,
            Running,
            Cycling,
            Hiking,
            Swimming,
        )
    }
}

public fun PersistentMap<Activity, Preferences>.remainingActivities(): PersistentList<Activity> =
    Activity.all
        .filterNot {
            it in this
        }.toPersistentList()
