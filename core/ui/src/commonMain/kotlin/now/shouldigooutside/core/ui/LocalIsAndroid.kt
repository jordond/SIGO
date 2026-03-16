package now.shouldigooutside.core.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import now.shouldigooutside.core.platform.isAndroid

public val LocalIsAndroid: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { isAndroid }
