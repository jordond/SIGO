package now.shouldigooutside.core.model.preferences

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentList

@Immutable
public sealed interface Activity {
    public val displayName: String

    public data object General : Activity {
        override val displayName: String = "General"
    }

    public data object Walking : Activity {
        override val displayName: String = "Walking"
    }

    public data object Running : Activity {
        override val displayName: String = "Running"
    }

    public data object Cycling : Activity {
        override val displayName: String = "Cycling"
    }

    public data object Hiking : Activity {
        override val displayName: String = "Hiking"
    }

    public data object Swimming : Activity {
        override val displayName: String = "Swimming"
    }

    public data class Custom(
        val name: String,
    ) : Activity {
        override val displayName: String = name

        public companion object {
            public const val MAX_NAME_LENGTH: Int = 12
        }
    }

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
