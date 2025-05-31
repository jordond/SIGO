@file:Suppress("FunctionName")

import androidx.compose.ui.window.ComposeUIViewController
import app.sigot.App
import app.sigot.di.initKoin
import platform.UIKit.UIViewController

@Suppress("unused")
fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}
