@file:Suppress("FunctionName")

import androidx.compose.ui.window.ComposeUIViewController
import app.sigot.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
