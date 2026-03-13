package app.sigot.core.platform

import androidx.compose.runtime.Composable
import coil3.PlatformContext

@Suppress("EnumEntryName")
public enum class Platform {
    Android,
    iOS,
    Desktop,
    Web,
}

internal expect val context: PlatformContext

public expect val isAndroid: Boolean

public expect val platform: Platform

@Composable
public expect fun context(): PlatformContext
