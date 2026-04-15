@file:Suppress("FunctionName")

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import now.shouldigooutside.App
import now.shouldigooutside.core.widget.IosWidgetUpdateObserver
import now.shouldigooutside.di.initKoin
import org.koin.mp.KoinPlatformTools
import platform.UIKit.UIApplication
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.UIViewController
import platform.UIKit.setStatusBarStyle

@Suppress("unused")
fun MainViewController(): UIViewController {
    ensureKoin()
    return ComposeUIViewController {
        App(onThemeChanged = { ThemeChanged(it) })
    }
}

@Suppress("unused")
fun widgetUpdateObserver(): IosWidgetUpdateObserver {
    ensureKoin()
    return KoinPlatformTools.defaultContext().get().get()
}

private fun ensureKoin() {
    if (KoinPlatformTools.defaultContext().getOrNull() == null) {
        initKoin()
    }
}

@Composable
private fun ThemeChanged(isDark: Boolean) {
    LaunchedEffect(isDark) {
        UIApplication.sharedApplication.setStatusBarStyle(
            if (isDark) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent,
        )
    }
}
