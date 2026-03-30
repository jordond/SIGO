package now.shouldigooutside.core.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import now.shouldigooutside.core.model.ui.AppExperience

public val LocalAppExperience: ProvidableCompositionLocal<AppExperience> = compositionLocalOf {
    error("No AppExperience provided")
}
