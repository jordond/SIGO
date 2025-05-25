package app.sigot.core.config.model

public data class UrlConfig(
    val root: String = ROOT,
    val privacy: String = PRIVACY,
    val terms: String = TERMS,
) {
    internal companion object Defaults {
        const val ROOT = "https://shouldigooutside.today"
        const val PRIVACY = "https://shouldigooutside.today/privacy"
        const val TERMS = "https://shouldigooutside.today/terms-and-conditions"
    }
}
