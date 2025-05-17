package app.sigot.core.ui.components.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

public val LocalSnackbarProvider: ProvidableCompositionLocal<SnackbarProvider> =
    compositionLocalOf {
        object : SnackbarProvider {
            override val hostState: SnackbarHostState = SnackbarHostState()

            override fun show(
                message: String,
                duration: SnackbarDuration,
                withDismissAction: Boolean,
            ) {
                // No-op
            }
        }
    }

public interface SnackbarProvider {
    public val hostState: SnackbarHostState

    public fun show(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Long,
        withDismissAction: Boolean = true,
    )
}

@Composable
public fun rememberSnackbarProvider(
    hostState: SnackbarHostState = remember { SnackbarHostState() },
): SnackbarProvider {
    val scope = rememberCoroutineScope()
    return remember(hostState) {
        hostState.snackbarProvider(scope)
    }
}

@Composable
public fun SnackbarProvider.show(
    message: StringResource,
    duration: SnackbarDuration = SnackbarDuration.Long,
    withDismissAction: Boolean = true,
) {
    show(stringResource(message), duration, withDismissAction)
}

internal fun SnackbarHostState.snackbarProvider(scope: CoroutineScope): SnackbarProvider =
    object : SnackbarProvider {
        override val hostState = this@snackbarProvider

        override fun show(
            message: String,
            duration: SnackbarDuration,
            withDismissAction: Boolean,
        ) {
            currentSnackbarData?.dismiss()
            scope.launch {
                show(message, duration = duration, withDismissAction = withDismissAction)
            }
        }
    }
