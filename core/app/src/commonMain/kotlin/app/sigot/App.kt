package app.sigot

import androidx.compose.runtime.Composable
import app.sigot.ui.AppHost
import org.koin.compose.KoinContext

@Composable
public fun App() {
    KoinContext {
        AppHost()
    }
}
