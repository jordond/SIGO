package app.sigot.core.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import app.sigot.core.platform.isAndroid

public val LocalIsAndroid: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { isAndroid }
