package app.sigot.core.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@Composable
public actual fun calculateWindowSizeClass(): WindowSizeClass {
    val activity = LocalActivity.current
    return calculateWindowSizeClass(activity ?: error("Activity is not available"))
}
