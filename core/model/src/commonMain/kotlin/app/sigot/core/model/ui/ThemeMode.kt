package app.sigot.core.model.ui

public enum class ThemeMode {
    Light,
    Dark,
    System,
    ;

    public companion object {
        public fun from(
            string: String,
            default: ThemeMode = System,
        ): ThemeMode =
            entries
                .firstOrNull { it.name.equals(string, ignoreCase = true) }
                ?: default
    }
}
