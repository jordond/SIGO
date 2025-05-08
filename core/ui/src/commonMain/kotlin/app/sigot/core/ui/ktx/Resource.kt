package app.sigot.core.ui.ktx

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
public fun StringResource.get(): String = stringResource(this)

@Composable
public fun StringResource.get(vararg formatArgs: Any): String = stringResource(this, *formatArgs)

@Composable
public fun DrawableResource.painter(): Painter = painterResource(this)

@Composable
public fun DrawableResource.image(): ImageBitmap = imageResource(this)
