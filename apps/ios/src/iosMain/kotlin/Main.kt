@file:Suppress("FunctionName")

import androidx.compose.ui.window.ComposeUIViewController
import now.shouldigooutside.App
import now.shouldigooutside.di.initKoin
import platform.UIKit.UIViewController

@Suppress("unused")
fun MainViewController(): UIViewController {
    initKoin()
    return ComposeUIViewController { App() }
}
