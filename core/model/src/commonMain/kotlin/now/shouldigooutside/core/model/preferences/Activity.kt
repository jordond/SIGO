package now.shouldigooutside.core.model.preferences

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
}
