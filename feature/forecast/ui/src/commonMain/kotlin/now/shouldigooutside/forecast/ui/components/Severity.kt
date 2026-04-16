package now.shouldigooutside.forecast.ui.components

import kotlinx.serialization.Serializable
import now.shouldigooutside.core.model.score.ReasonValue

@Serializable
public enum class Severity {
    Warning,
    Danger,
    ;

    public companion object {
        public fun fromReason(reason: ReasonValue): Severity? =
            when (reason) {
                ReasonValue.Inside -> null
                ReasonValue.Near -> Warning
                ReasonValue.Outside -> Danger
            }
    }
}
