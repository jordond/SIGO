package app.sigot.core.ui

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable

@Composable
public actual fun calculateWindowSizeClass(): WindowSizeClass =
    androidx.compose.material3.windowsizeclass
        .calculateWindowSizeClass()
