package now.shouldigooutside.baselineprofile.support

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until

internal fun MacrobenchmarkScope.seedBenchmark() {
    killProcess()
    @Suppress("DEPRECATION")
    clearAppData()
}

internal fun MacrobenchmarkScope.seedAndLaunch() {
    seedBenchmark()
    startActivityAndWait()
}

internal fun MacrobenchmarkScope.waitForHomeTabLoaded() {
    device.wait(Until.hasObject(By.res(Tags.HOME_CONTENT)), 10_000)
    device.wait(Until.hasObject(By.res(Tags.HOME_FORECAST_LIST)), 10_000)
}

internal fun MacrobenchmarkScope.switchAllTabs() {
    listOf("forecast", "activities", "preferences", "home").forEach { tab ->
        device.findObject(By.res(Tags.bottomNav(tab)))?.click()
        device.waitForIdle()
    }
}

internal fun MacrobenchmarkScope.scrollHomeForecastList() {
    val list = device.findObject(By.res(Tags.HOME_FORECAST_LIST)) ?: return
    repeat(3) {
        list.fling(Direction.DOWN)
        device.waitForIdle()
    }
    list.fling(Direction.UP)
    device.waitForIdle()
}

internal fun MacrobenchmarkScope.openForecastDetailsAndBack() {
    device.findObject(By.res(Tags.FORECAST_DETAIL_ENTRY))?.click()
    device.waitForIdle()
    device.pressBack()
    device.waitForIdle()
}
