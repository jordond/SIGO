package now.shouldigooutside.core.config.model

public data class UrlConfig(
    val root: String = ROOT,
    val privacy: String = PRIVACY,
    val terms: String = TERMS,
) {
    internal companion object Defaults {
        const val ROOT = "https://shouldigooutside.now"
        const val PRIVACY = "https://shouldigooutside.now/privacy"
        const val TERMS = "https://shouldigooutside.now/terms"
    }
}
