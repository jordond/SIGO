package now.shouldigooutside.core.ui.components.snackbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

public val LocalSnackbarProvider: ProvidableCompositionLocal<SnackbarProvider> =
    compositionLocalOf {
        object : SnackbarProvider {
            override val hostState: SnackbarHostState = SnackbarHostState()

            override fun show(
                message: String,
                duration: SnackbarDuration,
                withDismissAction: Boolean,
                type: SnackbarType,
            ) {
                // no -op
            }

            override fun show(
                message: StringResource,
                duration: SnackbarDuration,
                withDismissAction: Boolean,
                type: SnackbarType,
            ) {
                // no -op
            }

            override fun error(
                message: String,
                duration: SnackbarDuration,
                withDismissAction: Boolean,
            ) {
                // no -op
            }

            override fun error(
                message: StringResource,
                duration: SnackbarDuration,
                withDismissAction: Boolean,
            ) {
                // no -op
            }
        }
    }

public interface SnackbarProvider {
    public val hostState: SnackbarHostState

    public fun show(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Long,
        withDismissAction: Boolean = true,
        type: SnackbarType = SnackbarType.Primary,
    )

    public fun show(
        message: StringResource,
        duration: SnackbarDuration = SnackbarDuration.Long,
        withDismissAction: Boolean = true,
        type: SnackbarType = SnackbarType.Primary,
    )

    public fun error(
        message: String,
        duration: SnackbarDuration = SnackbarDuration.Long,
        withDismissAction: Boolean = true,
    )

    public fun error(
        message: StringResource,
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

internal fun SnackbarHostState.snackbarProvider(scope: CoroutineScope): SnackbarProvider =
    object : SnackbarProvider {
        override val hostState = this@snackbarProvider

        override fun show(
            message: String,
            duration: SnackbarDuration,
            withDismissAction: Boolean,
            type: SnackbarType,
        ) {
            currentSnackbarData?.dismiss()
            scope.launch {
                hostState.show(
                    message = message,
                    duration = duration,
                    withDismissAction = withDismissAction,
                    type = type,
                )
            }
        }

        override fun show(
            message: StringResource,
            duration: SnackbarDuration,
            withDismissAction: Boolean,
            type: SnackbarType,
        ) {
            scope.launch { show(getString(message), duration, withDismissAction, type) }
        }

        override fun error(
            message: String,
            duration: SnackbarDuration,
            withDismissAction: Boolean,
        ) {
            show(message, duration, withDismissAction, SnackbarType.Error)
        }

        override fun error(
            message: StringResource,
            duration: SnackbarDuration,
            withDismissAction: Boolean,
        ) {
            show(message, duration, withDismissAction, SnackbarType.Error)
        }
    }
