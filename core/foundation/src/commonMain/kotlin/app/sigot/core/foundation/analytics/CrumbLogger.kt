package app.sigot.core.foundation.analytics

public enum class CrumbLevel {
    Debug,
    Info,
    Warning,
    Error,
    Fatal,
}

public interface CrumbLogger {
    public fun add(
        message: String?,
        category: String? = null,
        level: CrumbLevel = CrumbLevel.Info,
        data: Map<String, Any>? = null,
    )
}
