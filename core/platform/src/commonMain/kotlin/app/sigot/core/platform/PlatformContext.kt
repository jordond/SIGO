package app.sigot.core.platform

import androidx.compose.runtime.Composable
import coil3.PlatformContext

public expect val context: PlatformContext

public expect val isAndroid: Boolean

@Composable
public expect fun context(): PlatformContext
